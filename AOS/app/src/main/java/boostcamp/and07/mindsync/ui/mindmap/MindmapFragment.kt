package boostcamp.and07.mindsync.ui.mindmap

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import boostcamp.and07.mindsync.R
import boostcamp.and07.mindsync.data.NodeGenerator
import boostcamp.and07.mindsync.data.model.CircleNode
import boostcamp.and07.mindsync.data.model.Node
import boostcamp.and07.mindsync.data.model.RectangleNode
import boostcamp.and07.mindsync.databinding.FragmentMindmapBinding
import boostcamp.and07.mindsync.ui.base.BaseFragment
import boostcamp.and07.mindsync.ui.dialog.EditDescriptionDialog
import boostcamp.and07.mindsync.ui.dialog.EditDialogInterface
import boostcamp.and07.mindsync.ui.util.Dp
import boostcamp.and07.mindsync.ui.util.Px
import boostcamp.and07.mindsync.ui.util.toDp
import boostcamp.and07.mindsync.ui.view.MindmapContainer
import boostcamp.and07.mindsync.ui.view.listener.NodeClickListener
import boostcamp.and07.mindsync.ui.view.listener.NodeUpdateListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MindmapFragment :
    BaseFragment<FragmentMindmapBinding>(R.layout.fragment_mindmap),
    NodeClickListener,
    NodeUpdateListener {

    private val mindMapViewModel: MindMapViewModel by viewModels()
    private val mindmapContainer = MindmapContainer()
    override fun initView() {
        setupRootNode()
        setBinding()
        collectHead()
        collectSelectedNode()
    }

    private fun setupRootNode() {
        val displayMetrics = requireActivity().resources.displayMetrics
        val screenHeight = Dp(Px(displayMetrics.heightPixels.toFloat()).toDp(requireContext()))
        mindMapViewModel.updateHead(screenHeight / 2)
    }

    private fun setBinding() {
        binding.vm = mindMapViewModel
        binding.view = this
        mindmapContainer.setNodeClickListener(this)
        mindmapContainer.setNodeUpdateListener(this)
        binding.zoomLayoutMindmapRoot.mindmapContainer = mindmapContainer
        binding.zoomLayoutMindmapRoot.initializeZoomLayout()
    }

    private fun collectHead() {
        viewLifecycleOwner.lifecycleScope.launch {
            mindMapViewModel.head.collectLatest { newHead ->
                mindmapContainer.updateHead(newHead)
                binding.zoomLayoutMindmapRoot.lineView.updateHead(mindmapContainer.head)
                binding.zoomLayoutMindmapRoot.nodeView.updateHead(mindmapContainer.head)
            }
        }
    }

    private fun collectSelectedNode() {
        viewLifecycleOwner.lifecycleScope.launch {
            mindMapViewModel.selectedNode.collectLatest { selectNode ->
                mindmapContainer.setSelectedNode(selectNode)
            }
        }
    }

    private fun showDialog(selectNode: Node, action: (Node, String) -> Unit) {
        val dialog = EditDescriptionDialog()
        dialog.setListener(object : EditDialogInterface {
            override fun onSubmitClick(description: String) {
                action.invoke(selectNode, description)
            }
        })
        dialog.show(requireActivity().supportFragmentManager, "EditDescriptionDialog")
    }

    fun addButtonListener(selectNode: Node) {
        showDialog(selectNode) { parent, description ->
            mindMapViewModel.addNode(parent, NodeGenerator.makeNode(description))
        }
    }

    fun editButtonListener(selectNode: Node) {
        showDialog(selectNode) { node, description ->
            val newNode = when (node) {
                is CircleNode -> node.copy(description = description)
                is RectangleNode -> node.copy(description = description)
            }
            mindMapViewModel.updateNode(newNode)
        }
    }

    override fun clickNode(node: Node?) {
        mindMapViewModel.setSelectedNode(node)
    }

    override fun updateHead(head: Node) {
        mindMapViewModel.updateHead(head)
    }
}
