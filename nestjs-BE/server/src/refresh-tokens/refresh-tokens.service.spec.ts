import { Test, TestingModule } from '@nestjs/testing';
import { RefreshTokensService } from './refresh-tokens.service';
import { PrismaService } from '../prisma/prisma.service';
import { JwtModule, JwtService } from '@nestjs/jwt';
import { PrismaClientKnownRequestError } from '@prisma/client/runtime/library';
import { ConfigModule } from '@nestjs/config';
import { getExpiryDate } from '../utils/date';

jest.useFakeTimers();

describe('RefreshTokensService', () => {
  let service: RefreshTokensService;
  let prisma: PrismaService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      imports: [JwtModule, ConfigModule.forRoot()],
      providers: [
        RefreshTokensService,
        {
          provide: PrismaService,
          useValue: {
            refreshToken: {
              create: jest.fn(),
              delete: jest.fn(),
            },
          },
        },
      ],
    })
      .overrideProvider(JwtService)
      .useValue({ sign: jest.fn() })
      .compile();

    service = module.get<RefreshTokensService>(RefreshTokensService);
    prisma = module.get<PrismaService>(PrismaService);
  });

  afterEach(() => {
    jest.clearAllTimers();
  });

  it('createRefreshToken created', async () => {
    const testToken = {
      id: 0,
      token: 'Token',
      expiryDate: getExpiryDate({ week: 2 }),
      userUuid: 'userId',
    };
    jest.spyOn(prisma.refreshToken, 'create').mockResolvedValue(testToken);

    const token = service.createRefreshToken('userId');

    await expect(token).resolves.toEqual(testToken);
  });

  it('deleteRefreshToken deleted', async () => {
    const testToken = {
      id: 0,
      token: 'Token',
      expiryDate: getExpiryDate({ week: 2 }),
      userUuid: 'userId',
    };
    jest.spyOn(prisma.refreshToken, 'delete').mockResolvedValue(testToken);

    const token = service.deleteRefreshToken(testToken.token);

    await expect(token).resolves.toEqual(testToken);
  });

  it('deleteRefreshToken not found', async () => {
    jest
      .spyOn(prisma.refreshToken, 'delete')
      .mockRejectedValue(
        new PrismaClientKnownRequestError(
          'An operation failed because it depends on one or more records that were required but not found. Record to delete not found.',
          { code: 'P2025', clientVersion: '' },
        ),
      );

    const token = service.deleteRefreshToken('Token');

    await expect(token).resolves.toBeNull();
  });
});