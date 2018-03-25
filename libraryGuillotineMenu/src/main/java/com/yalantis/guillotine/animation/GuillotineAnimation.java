package com.yalantis.guillotine.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yalantis.guillotine.interfaces.GuillotineListener;
import com.yalantis.guillotine.util.ActionBarInterpolator;
import com.yalantis.guillotine.util.GuillotineInterpolator;

public class GuillotineAnimation {
  private static final String ROTATION = "rotation";
  private static final float GUILLOTINE_CLOSED_ANGLE = -90f;
  private static final float GUILLOTINE_OPENED_ANGLE = 0f;
  private static final int DEFAULT_DURATION = 625;
  private static final float ACTION_BAR_ROTATION_ANGLE = 3f;

  private final View guillotineView;
  private final long duration;
  private final ObjectAnimator openingAnimation;
  private final ObjectAnimator closingAnimation;
  private final GuillotineListener listener;
  private final boolean isRightToLeftLayout;
  private final TimeInterpolator interpolator;
  private final View actionBarView;
  private final long delay;

  private boolean isOpening;
  private boolean isClosing;

  private GuillotineAnimation(GuillotineBuilder builder) {
    this.actionBarView = builder.actionBarView;
    this.listener = builder.guillotineListener;
    this.guillotineView = builder.guillotineView;
    this.duration = builder.duration > 0 ? builder.duration : DEFAULT_DURATION;
    this.delay = builder.startDelay;
    this.isRightToLeftLayout = builder.isRightToLeftLayout;
    this.interpolator =
        builder.interpolator == null ? new GuillotineInterpolator() : builder.interpolator;
    setUpOpeningView(builder.openingView);
    setUpClosingView(builder.closingView);
    this.openingAnimation = buildOpeningAnimation();
    this.closingAnimation = buildClosingAnimation();
    if (builder.isClosedOnStart) {
      guillotineView.setRotation(GUILLOTINE_CLOSED_ANGLE);
      guillotineView.setVisibility(View.INVISIBLE);
    }
  }

  public void open() {
    if (!isOpening) {
      openingAnimation.start();
    }
  }

  public void close() {
    if (!isClosing) {
      closingAnimation.start();
    }
  }

  private void setUpOpeningView(final View openingView) {
    if (actionBarView != null) {
      actionBarView.getViewTreeObserver()
          .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                actionBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
              } else {
                actionBarView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
              }
              actionBarView.setPivotX(calculatePivotX(openingView));
              actionBarView.setPivotY(calculatePivotY(openingView));
            }
          });
    }
    openingView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        open();
      }
    });
  }

  private void setUpClosingView(final View closingView) {
    guillotineView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
              guillotineView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
              guillotineView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
            guillotineView.setPivotX(calculatePivotX(closingView));
            guillotineView.setPivotY(calculatePivotY(closingView));
          }
        });

    closingView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        close();
      }
    });
  }

  private ObjectAnimator buildOpeningAnimation() {
    ObjectAnimator rotationAnimator = initAnimator(
        ObjectAnimator.ofFloat(guillotineView, ROTATION, GUILLOTINE_CLOSED_ANGLE,
            GUILLOTINE_OPENED_ANGLE));
    rotationAnimator.setInterpolator(interpolator);
    rotationAnimator.setDuration(duration);
    rotationAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        guillotineView.setVisibility(View.VISIBLE);
        isOpening = true;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        isOpening = false;
        Log.d("animation.java prev", "open");
        if (listener != null) {
          Log.d("animation.java", "open");
          listener.onGuillotineOpened();
        }
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    return rotationAnimator;
  }

  private ObjectAnimator buildClosingAnimation() {
    ObjectAnimator rotationAnimator = initAnimator(
        ObjectAnimator.ofFloat(guillotineView, ROTATION, GUILLOTINE_OPENED_ANGLE,
            GUILLOTINE_CLOSED_ANGLE));
    rotationAnimator.setDuration((long) (duration * GuillotineInterpolator.ROTATION_TIME));
    rotationAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        isClosing = true;
        guillotineView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        isClosing = false;
        guillotineView.setVisibility(View.GONE);
        startActionBarAnimation();
        Log.d("animation.java prev", "close");

        if (listener != null) {
          Log.d("animation.java", "close");
          listener.onGuillotineClosed();
        }
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    return rotationAnimator;
  }

  private void startActionBarAnimation() {
    ObjectAnimator actionBarAnimation =
        ObjectAnimator.ofFloat(actionBarView, ROTATION, GUILLOTINE_OPENED_ANGLE,
            ACTION_BAR_ROTATION_ANGLE);
    actionBarAnimation.setDuration((long) (duration * (GuillotineInterpolator.FIRST_BOUNCE_TIME
        + GuillotineInterpolator.SECOND_BOUNCE_TIME)));
    actionBarAnimation.setInterpolator(new ActionBarInterpolator());
    actionBarAnimation.start();
  }

  private ObjectAnimator initAnimator(ObjectAnimator animator) {
    animator.setStartDelay(delay);
    return animator;
  }

  private float calculatePivotY(View burger) {
    return burger.getTop() + burger.getHeight() / 2;
  }

  private float calculatePivotX(View burger) {
    return burger.getLeft() + burger.getWidth() / 2;
  }

  public static class GuillotineBuilder {
    private final View guillotineView;
    private final View openingView;
    private final View closingView;
    private View actionBarView;
    private GuillotineListener guillotineListener;
    private long duration;
    private long startDelay;
    private boolean isRightToLeftLayout;
    private TimeInterpolator interpolator;
    private boolean isClosedOnStart;

    public GuillotineBuilder(View guillotineView, View closingView, View openingView) {
      this.guillotineView = guillotineView;
      this.openingView = openingView;
      this.closingView = closingView;
    }

    public GuillotineBuilder setActionBarViewForAnimation(View view) {
      this.actionBarView = view;
      return this;
    }

    public GuillotineBuilder setGuillotineListener(GuillotineListener guillotineListener) {
      this.guillotineListener = guillotineListener;
      return this;
    }

    public GuillotineBuilder setDuration(long duration) {
      this.duration = duration;
      return this;
    }

    public GuillotineBuilder setStartDelay(long startDelay) {
      this.startDelay = startDelay;
      return this;
    }

    public GuillotineBuilder setRightToLeftLayout(boolean isRightToLeftLayout) {
      this.isRightToLeftLayout = isRightToLeftLayout;
      return this;
    }

    public GuillotineBuilder setInterpolator(TimeInterpolator interpolator) {
      this.interpolator = interpolator;
      return this;
    }

    public GuillotineBuilder setClosedOnStart(boolean isClosedOnStart) {
      this.isClosedOnStart = isClosedOnStart;
      return this;
    }

    public GuillotineAnimation build() {
      return new GuillotineAnimation(this);
    }
  }
}