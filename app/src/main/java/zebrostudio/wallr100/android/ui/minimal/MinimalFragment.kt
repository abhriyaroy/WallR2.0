package zebrostudio.wallr100.android.ui.minimal

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.afollestad.dragselectrecyclerview.DragSelectTouchListener
import com.afollestad.dragselectrecyclerview.Mode.RANGE
import com.afollestad.materialcab.MaterialCab
import com.afollestad.materialdialogs.MaterialDialog
import com.skydoves.colorpickerview.ColorPickerView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.minimalBottomLayout
import kotlinx.android.synthetic.main.activity_main.minimalBottomLayoutFab
import kotlinx.android.synthetic.main.activity_main.spinner
import kotlinx.android.synthetic.main.fragment_minimal.minimalFragmentRecyclerView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.ui.adapters.MinimalImageAdapter
import zebrostudio.wallr100.android.utils.RecyclerViewItemDecorator
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.successToast
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView
import javax.inject.Inject

class MinimalFragment : BaseFragment(), MinimalView {

  @Inject
  internal lateinit var presenter: MinimalPresenter
  private var minimalImageAdapter: MinimalImageAdapter? = null
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
    initRecyclerView()
    initBottomPanel()
    presenter.attachView(this)
    presenter.handleViewCreated()
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun showAppBar() {
    activity!!.findViewById<AppBarLayout>(R.id.appbar).setExpanded(true, true)
  }

  override fun updateAllItems() {
    minimalImageAdapter?.notifyDataSetChanged()
  }

  override fun showUnableToGetColorsErrorMessage() {
    context!!.let {
      it.errorToast(it.stringRes(R.string.minimal_fragment_unable_to_get_colors_error_message))
    }
  }

  override fun showGenericErrorMessage() {
    context!!.let {
      it.errorToast(it.stringRes(R.string.generic_error_message))
    }
  }

  override fun updateItemView(index: Int) {
    minimalImageAdapter?.notifyItemChanged(index)
  }

  override fun removeItemView(index: Int) {
    minimalImageAdapter?.notifyItemRemoved(index)
  }

  override fun showCab(size: Int) {
    MaterialCab.attach(activity as AppCompatActivity, R.id.cabStub) {
      menuRes = R.menu.minimal
      closeDrawableRes = R.drawable.ic_close_white
      titleColor = Color.WHITE
      title = context!!.stringRes(R.string.minimal_fragment_cab_title, size)

      onSelection {
        if (it.itemId == R.id.delete) {
          presenter.handleDeleteMenuItemClick()
        }
        true
      }

      onDestroy {
        presenter.handleCabDestroyed(true)
        true
      }
    }

  }

  override fun hideCab() {
    MaterialCab.destroy()
  }

  override fun showBottomPanelWithAnimation() {
    AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
      fillAfter = true
      setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
          activity?.spinner?.isEnabled = true
          activity?.minimalBottomLayout?.visible()
          activity?.minimalBottomLayout?.visible()
        }

        override fun onAnimationStart(animation: Animation) {
          activity?.minimalBottomLayout?.isClickable = true
        }
      })
    }.let {
      activity?.minimalBottomLayout?.startAnimation(it)
    }

    AnimationUtils.loadAnimation(context, R.anim.grow_circular_reveal).apply {
      fillAfter = true
      setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
          activity?.minimalBottomLayoutFab?.visible()
          activity?.minimalBottomLayoutFab?.isClickable = true
        }

        override fun onAnimationStart(animation: Animation) {
          activity?.minimalBottomLayoutFab?.isClickable = true
        }
      })
    }.let {
      activity?.minimalBottomLayoutFab?.startAnimation(it)
    }
  }

  override fun hideBottomLayoutWithAnimation() {
    AnimationUtils.loadAnimation(context, R.anim.slide_down).apply {
      fillAfter = true
      setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
          activity?.spinner?.isEnabled = false
          activity?.minimalBottomLayout?.gone()
        }

        override fun onAnimationStart(animation: Animation) {
          activity?.minimalBottomLayout?.isClickable = false
        }
      })
    }.let {
      activity?.minimalBottomLayout?.startAnimation(it)
    }

    AnimationUtils.loadAnimation(context, R.anim.shrink_reverse_circular_reveal).apply {
      fillAfter = true
      setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
          activity?.minimalBottomLayoutFab?.gone()
          activity?.minimalBottomLayoutFab?.isClickable = false
        }

        override fun onAnimationStart(animation: Animation) {

        }
      })
    }.let {
      activity?.minimalBottomLayoutFab?.startAnimation(it)
    }
  }

  override fun startSelection(position: Int) {
    touchListener?.setIsActive(true, position)
  }

  override fun showDeselectBeforeDeletionMessage(numberOfItemsToBeDeselected: Int) {
    context!!.let { context ->
      if (numberOfItemsToBeDeselected == 1) {
        context.stringRes(R.string.minimal_fragment_deselect_1_color_message)
      } else {
        context.stringRes(R.string.minimal_fragment_deselect_x_colors_message,
            numberOfItemsToBeDeselected)
      }.let {
        context.errorToast(it)
      }
    }
  }

  override fun showDeleteColorsErrorMessage() {
    context!!.let {
      it.errorToast(it.stringRes(R.string.minimal_fragment_delete_colors_error_message))
    }
  }

  override fun clearCabIfActive(renewView: Boolean) {
    if (MaterialCab.isActive) {
      MaterialCab.destroy()
    }
  }

  override fun showColorPickerDialogAndAttachColorPickerListener() {
    context!!.let {
      colorPickerDialog = MaterialDialog.Builder(it)
          .backgroundColor(it.colorRes(R.color.primary))
          .customView(R.layout.dialog_colorpicker, false)
          .contentColor(it.colorRes(R.color.white))
          .widgetColor(it.colorRes(R.color.accent))
          .positiveColor(it.colorRes(R.color.accent))
          .negativeColor(it.colorRes(R.color.accent))
          .positiveText(it.stringRes(R.string.minimal_fragment_color_picker_positive_text))
          .negativeText(it.stringRes(R.string.minimal_fragment_color_picker_negative_text))
          .onPositive { dialog, _ ->
            val colorPickerHexTextView =
                dialog.findViewById(R.id.colorPickerHexTextView) as TextView
            presenter.handleColorPickerPositiveClick(colorPickerHexTextView.text as String)
          }
          .build()
    }
    colorPickerDialog?.show()

    colorPickerDialog?.customView?.apply {
      findViewById<ColorPickerView>(R.id.colorPickerView).let { colorPickerView ->
        colorPickerView.setColorListener {
          findViewById<TextView>(R.id.colorPickerHexTextView).text = "#${colorPickerView.colorHtml}"
          findViewById<View>(R.id.sampleColorView).setBackgroundColor(it)
        }
      }
    }
  }

  override fun addColorAndScrollToItemView(index: Int) {
    if (colorPickerDialog?.isShowing == true) {
      colorPickerDialog?.dismiss()
    }
    minimalImageAdapter?.notifyItemInserted(index)
    minimalFragmentRecyclerView.smoothScrollToPosition(index)
  }

  override fun showAddColorSuccessMessage() {
    context!!.let {
      it.successToast(it.stringRes(R.string.minimal_fragment_add_color_success_message))
    }
  }

  override fun showColorAlreadyPresentErrorMessage(position: Int) {
    context!!.let {
      it.errorToast(it.stringRes(R.string.minimal_fragment_color_alreaady_present_error_message))
    }
    minimalFragmentRecyclerView?.smoothScrollToPosition(position)
  }

  override fun showExitSelectionModeToAddColorMessage() {
    context!!.let {
      it.infoToast(it.stringRes(R.string.minimal_fragment_exit_selection_before_adding_message))
    }
  }

  private fun initRecyclerView() {
    GridLayoutManager(context,
        context!!.integerRes(R.integer.minimal_image_recycler_view_span_count)).let {
      minimalFragmentRecyclerView.layoutManager = it
    }
    minimalFragmentRecyclerView.addItemDecoration(
        RecyclerViewItemDecorator(context!!.integerRes(R.integer.recycler_view_grid_spacing_px),
            context!!.integerRes(R.integer.minimal_image_recycler_view_grid_size)))
    minimalImageAdapter = MinimalImageAdapter(presenter)
    minimalFragmentRecyclerView.adapter = minimalImageAdapter
    touchListener = DragSelectTouchListener.create(context!!, minimalImageAdapter!!) {
      this.mode = RANGE
    }
    minimalFragmentRecyclerView.addOnItemTouchListener(touchListener!!)
    minimalFragmentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        presenter.handleOnScrolled(dy)
      }
    })
  }

  private fun initBottomPanel() {
    activity?.spinner?.setItems(
        context!!.stringRes(R.string.minimal_fragment_spinner_item_material),
        context!!.stringRes(R.string.minimal_fragment_spinner_item_gradient),
        context!!.stringRes(R.string.minimal_fragment_spinner_item_plasma))
    activity?.spinner?.setOnItemSelectedListener { _, position, _, _ ->
      presenter.handleSpinnerOptionChanged(position)
    }
  }

  companion object {
    fun newInstance() = MinimalFragment()
  }

}