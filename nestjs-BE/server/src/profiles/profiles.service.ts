import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateProfileDto } from './dto/update-profile.dto';
import { CreateProfileDto } from './dto/create-profile.dto';
import { Profile, Prisma } from '@prisma/client';
import generateUuid from '../utils/uuid';

@Injectable()
export class ProfilesService {
  constructor(private prisma: PrismaService) {}

  async findProfile(userUuid: string): Promise<Profile | null> {
    return this.prisma.profile.findUnique({ where: { user_id: userUuid } });
  }

  async findProfiles(profileUuids: string[]): Promise<Profile[]> {
    return this.prisma.profile.findMany({
      where: { uuid: { in: profileUuids } },
    });
  }

  async getOrCreateProfile(data: CreateProfileDto): Promise<Profile> {
    return this.prisma.profile.upsert({
      where: { user_id: data.user_id },
      update: {},
      create: {
        uuid: generateUuid(),
        user_id: data.user_id,
        image: data.image,
        nickname: data.nickname,
      },
    });
  }

  async updateProfile(
    userUuid: string,
    updateProfileDto: UpdateProfileDto,
  ): Promise<Profile | null> {
    try {
      return await this.prisma.profile.update({
        where: { user_id: userUuid },
        data: { ...updateProfileDto },
      });
    } catch (err) {
      if (err instanceof Prisma.PrismaClientKnownRequestError) {
        return null;
      } else {
        throw err;
      }
    }
  }
}
