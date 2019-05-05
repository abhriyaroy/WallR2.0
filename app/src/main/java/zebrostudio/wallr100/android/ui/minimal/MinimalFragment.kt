package zebrostudio.wallr100.android.ui.minimal

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.dragselectrecyclerview.DragSelectTouchListener
import com.afollestad.dragselectrecyclerview.Mode.RANGE
import com.afollestad.materialcab.MaterialCab
import com.afollestad.materialdialogs.MaterialDialog
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.minimalBottomLayout
import kotlinx.android.synthetic.main.activity_main.minimalBottomLayoutFab
import kotlinx.android.synthetic.main.activity_main.spinner
import kotlinx.android.synthetic.main.fragment_minimal.minimalFragmentRecyclerView
import kotlinx.android.synthetic.main.fragment_minimal.minimalFragmentRootLayout
import kotlinx.android.synthetic.main.toolbar_layout.toolbarMultiSelectIcon
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.ui.adapters.DragSelectImageAdapter
import zebrostudio.wallr100.android.ui.adapters.DragSelectImageAdapterCallbacks
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailActivity
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.MULTIPLE
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.SINGLE
import zebrostudio.wallr100.android.utils.RecyclerViewItemDecorator
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.menuTitleToast
import zebrostudio.wallr100.android.utils.showAnimation
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.successToast
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerContract.DragSelectItemPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType
import javax.inject.Inject

const val SINGLE_ITEM_SIZE = 1
const val BOTTOM_OFFSET = 3

class MinimalFragment : BaseFragment(), MinimalView {

  @Inject internal lateinit var presenter: MinimalPresenter
  @Inject internal lateinit var recyclerPresenter: DragSelectItemPresenter
  private var dragSelectImageAdapter: DragSelectImageAdapter? = null
  private var touchListener: DragSelectTouchListener? = null
  private var colorPickerDialog: MaterialDialog? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    AndroidSupportInjection.inject(this)
    return container?.inflate(inflater, R.layout.fragment_minimal)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.attachView(this)
    initRecyclerView()
    initBottomPanel()
    attachMultiSelectClickListener()
    presenter.handleViewCreated()
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun showAppBar() {
    activity?.findViewById<AppBarLayout>(R.id.appbar)?.setExpanded(true, true)
  }

  override fun updateAllItems() {
    dragSelectImageAdapter?.notifyDataSetChanged()
  }

  override fun showUnableToGetColorsErrorMessage() {
    errorToast(stringRes(R.string.minimal_fragment_unable_to_get_colors_error_message))
  }

  override fun showGenericErrorMessage() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  override fun updateItemView(index: Int) {
    dragSelectImageAdapter?.notifyItemChanged(index)
  }

  override fun removeItemView(index: Int) {
    dragSelectImageAdapter?.notifyItemRemoved(index)
  }

  override fun addItemView(index: Int) {
    dragSelectImageAdapter?.notifyItemInserted(index)
  }

  override fun showCab(size: Int) {
    MaterialCab.attach(activity as AppCompatActivity, R.id.cabStub) {
      menuRes = R.menu.minimal
      closeDrawableRes = R.drawable.ic_close_white
      titleColor = colorRes(R.color.white)
      title = stringRes(R.string.minimal_fragment_cab_title, size)
      backgroundColor = colorRes(R.color.primary)
      backgroundColorRes(R.color.primary)


      onSelection {
        if (it.itemId == R.id.delete) {
          presenter.handleDeleteMenuItemClick(dragSelectImageAdapter!!.getColorList(),
              dragSelectImageAdapter!!.getSelectedItemsMap())
        }
        true
      }

      onDestroy {
        presenter.handleCabDestroyed()
        true
      }
    }

  }

  override fun hideCab() {
    MaterialCab.destroy()
  }

  override fun showBottomPanelWithAnimation() {
    activity?.let {
      it.minimalBottomLayout?.showAnimation(R.anim.slide_up, onAnimationEnd = {
        it.spinner?.isEnabled = true
        it.minimalBottomLayout?.apply {
          visible()
          isClickable = true
        }
      })

      it.minimalBottomLayoutFab?.showAnimation(R.anim.grow_circular_reveal, onAnimationEnd = {
        it.minimalBottomLayoutFab?.apply {
          visible()
          isClickable = true
        }
      })
    }

  }

  override fun hideBottomLayoutWithAnimation() {
    activity?.let {
      it.minimalBottomLayout?.showAnimation(R.anim.slide_down, onAnimationStart = {
        it.minimalBottomLayout?.isClickable = false
      }, onAnimationEnd = {
        it.spinner?.isEnabled = false
        it.minimalBottomLayout?.gone()
      })

      it.minimalBottomLayoutFab?.showAnimation(R.anim.shrink_reverse_circular_reveal,
          onAnimationEnd = {
            it.minimalBottomLayoutFab?.apply {
              gone()
              isClickable = false
            }
          })
    }
  }

  override fun startSelection(position: Int) {
    touchListener?.setIsActive(true, position)
  }

  override fun showDeselectBeforeDeletionMessage(numberOfItemsToBeDeselected: Int) {
    if (numberOfItemsToBeDeselected == 1) {
      stringRes(R.string.minimal_fragment_deselect_1_color_message)
    } else {
      stringRes(R.string.minimal_fragment_deselect_x_colors_message,
          numberOfItemsToBeDeselected)
    }.let {
      errorToast(it)
    }
  }

  override fun showDeleteColorsErrorMessage() {
    errorToast(stringRes(R.string.minimal_fragment_delete_colors_error_message))
  }

  override fun clearCabIfActive() {
    if (MaterialCab.isActive) {
      MaterialCab.destroy()
    }
  }

  override fun showColorPickerDialogAndAttachColorPickerListener() {
    colorPickerDialog = MaterialDialog.Builder(context!!)
        .backgroundColor(colorRes(R.color.primary))
        .customView(R.layout.dialog_color_picker, false)
        .contentColor(colorRes(R.color.white))
        .widgetColor(colorRes(R.color.accent))
        .positiveColor(colorRes(R.color.accent))
        .negativeColor(colorRes(R.color.accent))
        .positiveText(stringRes(R.string.minimal_fragment_color_picker_positive_text))
        .negativeText(stringRes(R.string.minimal_fragment_color_picker_negative_text))
        .onPositive { dialog, _ ->
          val colorPickerHexTextView =
              dialog.findViewById(R.id.colorPickerHexTextView) as TextView
          presenter.handleColorPickerPositiveClick(colorPickerHexTextView.text as String,
              dragSelectImageAdapter!!.getColorList())
        }
        .build()
    colorPickerDialog?.show()

    colorPickerDialog?.customView?.apply {
      findViewById<ColorPickerView>(R.id.colorPickerView).let { colorPickerView ->
        colorPickerView.setColorListener(object : ColorEnvelopeListener {
          override fun onColorSelected(envelope: ColorEnvelope, fromUser: Boolean) {
            findViewById<TextView>(R.id.colorPickerHexTextView).text = "#${envelope.hexCode}"
            findViewById<View>(R.id.sampleColorView).setBackgroundColor(envelope.color)
          }
        })
      }
    }
  }

  override fun insertItemAndScrollToItemView(index: Int) {
    if (colorPickerDialog?.isShowing == true) {
      colorPickerDialog?.dismiss()
    }
    dragSelectImageAdapter?.notifyItemInserted(index)
    minimalFragmentRecyclerView.smoothScrollToPosition(index)
  }

  override fun showAddColorSuccessMessage() {
    successToast(stringRes(R.string.minimal_fragment_add_color_success_message))
  }

  override fun showColorAlreadyPresentErrorMessageAndScrollToPosition(position: Int) {
    errorToast(stringRes(R.string.minimal_fragment_color_alreaady_present_error_message))
    minimalFragmentRecyclerView?.smoothScrollToPosition(position)
  }

  override fun showExitSelectionModeToAddColorMessage() {
    infoToast(stringRes(R.string.minimal_fragment_exit_selection_before_adding_message))
  }

  override fun showUndoDeletionOption(size: Int) {
    if (size == SINGLE_ITEM_SIZE) {
      stringRes(R.string.minimal_fragment_single_item_deletion_successful_message)
    } else {
      stringRes(R.string.minimal_fragment_multiple_item_deletion_successful_message, size)
    }.let {
      Snackbar.make(minimalFragmentRootLayout, it, Snackbar.LENGTH_LONG).apply {
        setAction(context.stringRes(R.string.minimal_fragment_deletion_undo_action_text)
        ) {
          presenter.handleUndoDeletionOptionClick()
          dismiss()
        }
      }.show()
    }
  }

  override fun showUnableToRestoreColorsMessage() {
    errorToast(stringRes(R.string.minimal_fragment_unable_to_restore_colors_error_message))
  }

  override fun showRestoreColorsSuccessMessage() {
    successToast(stringRes(R.string.minimal_fragment_restore_colors_success_message))
  }

  override fun getTopAndBottomVisiblePositions(): Pair<Int, Int> {
    return (minimalFragmentRecyclerView.layoutManager as GridLayoutManager).let {
      Pair(it.findFirstCompletelyVisibleItemPosition(),
          it.findLastCompletelyVisibleItemPosition() - BOTTOM_OFFSET)
    }
  }

  override fun addToSelectedItemsMap(position: Int, hexValue: String) {
    dragSelectImageAdapter?.addToSelectedItemsMap(position, hexValue)
  }

  override fun removeFromSelectedItemsMap(item: Int) {
    dragSelectImageAdapter?.removeItemFromSelectedItemsMap(item)
  }

  override fun clearSelectedItemsMap() {
    dragSelectImageAdapter?.clearSelectedItemsMap()
  }

  override fun setColorList(list: List<String>) {
    dragSelectImageAdapter?.setColorList(list)
  }

  override fun addColorToList(hexValue: String) {
    dragSelectImageAdapter?.addColorToList(hexValue)
  }

  override fun selectItem(position: Int) {
    dragSelectImageAdapter?.setSelected(position, true)
  }

  override fun showColorDetails(hexValue: String) {
    context?.let {
      startActivity(ColorsDetailActivity.getCallingIntent(it, listOf(hexValue), SINGLE))
    }
  }

  override fun showMultiColorDetails(hexValueList: List<String>, type: MultiColorImageType) {
    context?.let {
      startActivity(ColorsDetailActivity.getCallingIntent(it, hexValueList, MULTIPLE, type))
    }
  }

  private fun initRecyclerView() {
    minimalFragmentRecyclerView?.apply {
      GridLayoutManager(context,
          integerRes(R.integer.minimal_image_recycler_view_span_count)).let {
        layoutManager = it
      }
      addItemDecoration(
          RecyclerViewItemDecorator(integerRes(R.integer.recycler_view_grid_spacing_px),
              integerRes(R.integer.minimal_image_recycler_view_grid_size)))
      dragSelectImageAdapter =
          DragSelectImageAdapter(getDragSelectRecyclerViewCallback(), recyclerPresenter)
      adapter = dragSelectImageAdapter
      touchListener = DragSelectTouchListener.create(context!!, dragSelectImageAdapter!!) {
        this.mode = RANGE
      }
      addOnItemTouchListener(touchListener!!)
      addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          super.onScrolled(recyclerView, dx, dy)
          presenter.handleOnScrolled(dy)
        }
      })
    }
  }

  private fun initBottomPanel() {
    activity?.spinner?.apply {
      setItems(
          stringRes(R.string.minimal_fragment_spinner_item_material),
          stringRes(R.string.minimal_fragment_spinner_item_gradient),
          stringRes(R.string.minimal_fragment_spinner_item_plasma))
      // To guarantee popup opening upwards
      setDropdownHeight(Int.MAX_VALUE)
      setOnItemSelectedListener { _, position, _, _ ->
        presenter.handleSpinnerOptionChanged(position)
      }
    }
  }

  private fun attachMultiSelectClickListener() {
    activity!!.let {
      it.toolbarMultiSelectIcon.apply {
        setOnClickListener {
          presenter.handleMultiSelectMenuClick()
        }

        setOnLongClickListener { view ->
          view.menuTitleToast(context!!,
              stringRes(R.string.minimal_fragment_toolbar_menu_multiselect_title),
              activity!!.window)
          true
        }
      }

      it.minimalBottomLayoutFab.setOnClickListener {
        presenter.handleMultiSelectFabClick(dragSelectImageAdapter!!.getSelectedItemsMap())
      }
    }
  }

  private fun getDragSelectRecyclerViewCallback() = object : DragSelectImageAdapterCallbacks {
    override fun setItemSelected(index: Int, selected: Boolean) {
      presenter.setItemSelected(index, selected, dragSelectImageAdapter!!.getColorList(),
          dragSelectImageAdapter!!.getSelectedItemsMap())
    }

    override fun isItemSelected(index: Int): Boolean {
      return presenter.isItemSelected(index, dragSelectImageAdapter!!.getSelectedItemsMap())
    }

    override fun isItemSelectable(index: Int): Boolean {
      return presenter.isItemSelectable(index)
    }

    override fun handleClick(index: Int) {
      return presenter.handleClick(index, dragSelectImageAdapter!!.getColorList(),
          dragSelectImageAdapter!!.getSelectedItemsMap())
    }

    override fun handleLongClick(index: Int): Boolean {
      return presenter.handleImageLongClick(index, dragSelectImageAdapter!!.getColorList(),
          dragSelectImageAdapter!!.getSelectedItemsMap())
    }

  }

  companion object {
    fun newInstance() = MinimalFragment()
  }

}