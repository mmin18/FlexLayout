package com.github.mmin18.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.github.mmin18.flexlayout.R;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by mmin18 on 2/14/16.
 * <p/>
 * layout_left, layout_right, layout_top, layout_bottom, layout_centerX, layout_centerY, layout_width2, layout_height2
 * wrap_content, match_parent, 100%, 50%+100dp
 * this.width*1.6, prev.right+10dp, next.left-10dp
 * parent.height/2 (only parent.width and parent.height are supported)
 * other_view.width*0.5-30px (other_view is defined as res id)
 * screen.width, screen.height (screen width/height is relative to current orientation)
 * (prev.width-10dp)/2
 * max(view1.right,view2.right), min(), round(), ceil(), floor(), abs(), mod(), pow()
 */
public class FlexLayout extends ViewGroup {
	public FlexLayout(Context context) {
		this(context, null);
	}

	public FlexLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlexLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	int myWidth;
	int myHeight;

	public static class LayoutParams extends ViewGroup.LayoutParams {
		static int UNSPECIFIED = -5;

		RPN left, right, top, bottom;
		RPN centerX, centerY;
		RPN width2, height2;

		float mLeft, mRight, mTop, mBottom;
		float mCenterX, mCenterY;
		float mWidth, mHeight;
		int mMeasuredWidth, mMeasuredHeight;

		static final int[] ViewGroup_Layout = new int[]{android.R.attr.layout_width, android.R.attr.layout_height};

		public LayoutParams(Context c, AttributeSet attrs) {
			super(0, 0);
			TypedArray a = c.obtainStyledAttributes(attrs, ViewGroup_Layout);
			width = a.getLayoutDimension(0, UNSPECIFIED);
			height = a.getLayoutDimension(1, UNSPECIFIED);
			a.recycle();

			a = c.obtainStyledAttributes(attrs, R.styleable.FlexLayout_Layout);
			this.left = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_left));
			this.top = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_top));
			this.right = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_right));
			this.bottom = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_bottom));
			this.centerX = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_centerX));
			this.centerY = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_centerY));
			this.width2 = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_width2));
			this.height2 = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_height2));
			a.recycle();
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

		void reset() {
			mLeft = Float.NaN;
			mRight = Float.NaN;
			mTop = Float.NaN;
			mBottom = Float.NaN;
			mCenterX = Float.NaN;
			mCenterY = Float.NaN;
			mWidth = Float.NaN;
			mHeight = Float.NaN;
			mMeasuredWidth = -1;
			mMeasuredHeight = -1;
		}

		float getLeft() {
			if (mLeft == mLeft) {
				return mLeft;
			}
			if (mWidth == mWidth) {
				if (mRight == mRight) {
					return mRight - mWidth;
				}
				if (mCenterX == mCenterX) {
					return mCenterX - mWidth / 2;
				}
			}
			if (mCenterX == mCenterX && mRight == mRight) {
				return 2 * mCenterX - mRight;
			}
			return Float.NaN;
		}

		float getRight() {
			if (mRight == mRight) {
				return mRight;
			}
			if (mWidth == mWidth) {
				if (mLeft == mLeft) {
					return mLeft + mWidth;
				}
				if (mCenterX == mCenterX) {
					return mCenterX + mWidth / 2;
				}
			}
			if (mCenterX == mCenterX && mLeft == mLeft) {
				return 2 * mCenterX - mLeft;
			}
			return Float.NaN;
		}

		float getTop() {
			if (mTop == mTop) {
				return mTop;
			}
			if (mHeight == mHeight) {
				if (mBottom == mBottom) {
					return mBottom - mHeight;
				}
				if (mCenterY == mCenterY) {
					return mCenterY - mHeight / 2;
				}
			}
			if (mCenterY == mCenterY && mBottom == mBottom) {
				return 2 * mCenterY - mBottom;
			}
			return Float.NaN;
		}

		float getBottom() {
			if (mBottom == mBottom) {
				return mBottom;
			}
			if (mHeight == mHeight) {
				if (mTop == mTop) {
					return mTop + mHeight;
				}
				if (mCenterY == mCenterY) {
					return mCenterY + mHeight / 2;
				}
			}
			if (mCenterY == mCenterY && mTop == mTop) {
				return 2 * mCenterY - mTop;
			}
			return Float.NaN;
		}

		float getCenterX() {
			if (mCenterX == mCenterX) {
				return mCenterX;
			}
			if (mWidth == mWidth) {
				if (mLeft == mLeft) {
					return mLeft + mWidth / 2;
				}
				if (mRight == mRight) {
					return mRight - mWidth / 2;
				}
			}
			if (mLeft == mLeft && mRight == mRight) {
				return (mLeft + mRight) / 2;
			}
			return Float.NaN;
		}

		float getCenterY() {
			if (mCenterY == mCenterY) {
				return mCenterY;
			}
			if (mHeight == mHeight) {
				if (mTop == mTop) {
					return mTop + mHeight / 2;
				}
				if (mBottom == mBottom) {
					return mBottom - mHeight / 2;
				}
			}
			if (mTop == mTop && mBottom == mBottom) {
				return (mTop + mBottom) / 2;
			}
			return Float.NaN;
		}

		float getWidth() {
			if (mWidth == mWidth) {
				return mWidth;
			}
			if (mLeft == mLeft) {
				if (mRight == mRight) {
					return mRight - mLeft;
				}
				if (mCenterX == mCenterX) {
					return (mCenterX - mLeft) * 2;
				}
			}
			if (mRight == mRight && mCenterX == mCenterX) {
				return (mRight - mCenterX) * 2;
			}
			return Float.NaN;
		}

		float getHeight() {
			if (mHeight == mHeight) {
				return mHeight;
			}
			if (mTop == mTop) {
				if (mBottom == mBottom) {
					return mBottom - mTop;
				}
				if (mCenterY == mCenterY) {
					return (mCenterY - mTop) * 2;
				}
			}
			if (mBottom == mBottom && mCenterY == mCenterY) {
				return (mBottom - mCenterY) * 2;
			}
			return Float.NaN;
		}

		boolean isValidH() {
			int c = 0;
			if (mLeft == mLeft)
				c++;
			if (mRight == mRight)
				c++;
			if (mWidth == mWidth)
				c++;
			if (mCenterX == mCenterX)
				c++;
			return c >= 2;
		}

		boolean isValidV() {
			int c = 0;
			if (mTop == mTop)
				c++;
			if (mBottom == mBottom)
				c++;
			if (mHeight == mHeight)
				c++;
			if (mCenterY == mCenterY)
				c++;
			return c >= 2;
		}

		boolean isValid() {
			return isValidH() && isValidV();
		}
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new FlexLayout.LayoutParams(getContext(), attrs);
	}

	/**
	 * Returns a set of layout parameters with a width of
	 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
	 * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and no spanning.
	 */
	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new FlexLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	// Override to allow type-checking of LayoutParams.
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof FlexLayout.LayoutParams;
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new FlexLayout.LayoutParams(p);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int maxWidth, maxHeight;
		if (widthMode == MeasureSpec.EXACTLY) {
			myWidth = maxWidth = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			myWidth = -1;
			maxWidth = widthSize;
		} else {
			myWidth = -1;
			maxWidth = -1;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			myHeight = maxHeight = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			myHeight = -1;
			maxHeight = heightSize;
		} else {
			myHeight = -1;
			maxHeight = -1;
		}

		final int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) child.getLayoutParams();
			lp.reset();
			if (child.getVisibility() == View.GONE) {
				lp.mWidth = 0;
				lp.mHeight = 0;
			}
			if (lp.left == null) {
				int c = 0;
				if (lp.right != null)
					c++;
				if (lp.centerX != null)
					c++;
				if (lp.width2 != null || lp.width != LayoutParams.UNSPECIFIED)
					c++;
				if (c < 2)
					lp.mLeft = 0;
			}
			if (lp.top == null) {
				int c = 0;
				if (lp.bottom != null)
					c++;
				if (lp.centerY != null)
					c++;
				if (lp.height2 != null || lp.height != LayoutParams.UNSPECIFIED)
					c++;
				if (c < 2)
					lp.mTop = 0;
			}
		}

		boolean ready = false;
		for (int j = 0; j < count * 4; j++) {
			int vCount = 0, invIndex = -1;
			int calcCount = 0;
			for (int i = 0; i < count; i++) {
				View child = getChildAt(i);
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) child.getLayoutParams();

				if (lp.left != null && lp.mLeft != lp.mLeft) {
					float v = lp.left.eval(this, i, 0);
					if (v == v) {
						lp.mLeft = v;
						calcCount++;
					}
				}
				if (lp.right != null && lp.mRight != lp.mRight) {
					float v = lp.right.eval(this, i, 0);
					if (v == v) {
						lp.mRight = v;
						calcCount++;
					}
				}
				if (lp.top != null && lp.mTop != lp.mTop) {
					float v = lp.top.eval(this, i, 1);
					if (v == v) {
						lp.mTop = v;
						calcCount++;
					}
				}
				if (lp.bottom != null && lp.mBottom != lp.mBottom) {
					float v = lp.bottom.eval(this, i, 1);
					if (v == v) {
						lp.mBottom = v;
						calcCount++;
					}
				}
				if (lp.centerX != null && lp.mCenterX != lp.mCenterX) {
					float v = lp.centerX.eval(this, i, 0);
					if (v == v) {
						lp.mCenterX = v;
						calcCount++;
					}
				}
				if (lp.centerY != null && lp.mCenterY != lp.mCenterY) {
					float v = lp.centerY.eval(this, i, 1);
					if (v == v) {
						lp.mCenterY = v;
						calcCount++;
					}
				}
				if (lp.mWidth != lp.mWidth) {
					if (lp.width2 != null) {
						float v = lp.width2.eval(this, i, 0);
						if (v == v) {
							lp.mWidth = v;
							calcCount++;
						}
					} else if (lp.width != LayoutParams.UNSPECIFIED) {
						if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT && myWidth != -1) {
							lp.mWidth = myWidth;
							calcCount++;
						} else if (lp.width >= 0) {
							lp.mWidth = lp.width;
							calcCount++;
						} else {
							if (lp.mMeasuredWidth == -1 && measureChild(widthMeasureSpec, heightMeasureSpec, child, lp)) {
								calcCount++;
							}
							if (lp.mMeasuredWidth != -1 && lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
								lp.mWidth = lp.mMeasuredWidth;
								calcCount++;
							}
						}
					}
				}
				if (lp.mHeight != lp.mHeight) {
					if (lp.height2 != null) {
						float v = lp.height2.eval(this, i, 1);
						if (v == v) {
							lp.mHeight = v;
							calcCount++;
						}
					} else if (lp.height != LayoutParams.UNSPECIFIED) {
						if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT && myHeight != -1) {
							lp.mHeight = myHeight;
							calcCount++;
						} else if (lp.height >= 0) {
							lp.mHeight = lp.height;
							calcCount++;
						} else {
							if (lp.mMeasuredHeight == -1 && measureChild(widthMeasureSpec, heightMeasureSpec, child, lp)) {
								calcCount++;
							}
							if (lp.mMeasuredHeight != -1 && lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
								lp.mHeight = lp.mMeasuredHeight;
								calcCount++;
							}
						}
					}
				}

				if (lp.isValid()) {
					vCount++;
				} else if (invIndex == -1) {
					invIndex = i;
				}
			}

			if (vCount == count && myWidth != -1 && myHeight != -1) {
				ready = true;
				break;
			}
			if (calcCount == 0) {
				if (myWidth == -1 || myHeight == -1) {
					int maxw = 0;
					int maxh = 0;
					for (int i = 0; i < count; i++) {
						View child = getChildAt(i);
						FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) child.getLayoutParams();
						float r = lp.getRight();
						if (r == r) {
							maxw = Math.max(maxw, Math.round(r));
						} else if (lp.mMeasuredWidth != -1) {
							float l = lp.getLeft();
							if (l == l) {
								maxw = Math.max(maxw, Math.round(l + lp.mMeasuredWidth));
							} else {
								maxw = Math.max(maxw, lp.mMeasuredWidth);
							}
						}
						float b = lp.getBottom();
						if (b == b) {
							maxh = Math.max(maxh, Math.round(b));
						} else if (lp.mMeasuredHeight != -1) {
							float t = lp.getTop();
							if (t == t) {
								maxh = Math.max(maxh, Math.round(t + lp.mMeasuredHeight));
							} else {
								maxh = Math.max(maxh, lp.mMeasuredHeight);
							}
						}

						lp.mMeasuredWidth = -1;
						lp.mMeasuredHeight = -1;
					}
					if (myWidth == -1) {
						myWidth = maxWidth == -1 ? maxw : Math.min(maxw, maxWidth);
					}
					if (myHeight == -1) {
						myHeight = maxHeight == -1 ? maxh : Math.min(maxh, maxHeight);
					}
				} else {
					throw new RuntimeException("incomplete layout (" + invIndex + ")");
				}
			}
		}

		if (!ready) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < count; i++) {
				View child = getChildAt(i);
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) child.getLayoutParams();
				if (!lp.isValid()) {
					if (sb.length() > 0) {
						sb.append(',');
					}
					sb.append(i);
				}
			}
			throw new RuntimeException("incomplete layout (" + sb + ")");
		}

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) child.getLayoutParams();

			int mwspec, mhspec;
			if (lp.width2 != null && lp.mWidth == lp.mWidth) {
				mwspec = MeasureSpec.makeMeasureSpec(Math.round(lp.mWidth), MeasureSpec.EXACTLY);
			} else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
				mwspec = MeasureSpec.makeMeasureSpec(myWidth, MeasureSpec.AT_MOST);
			} else if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
				mwspec = MeasureSpec.makeMeasureSpec(myWidth, MeasureSpec.EXACTLY);
			} else {
				mwspec = MeasureSpec.makeMeasureSpec(Math.round(lp.getWidth()), MeasureSpec.EXACTLY);
			}
			if (lp.height2 != null && lp.mHeight == lp.mHeight) {
				mhspec = MeasureSpec.makeMeasureSpec(Math.round(lp.mHeight), MeasureSpec.EXACTLY);
			} else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
				mhspec = MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.AT_MOST);
			} else if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
				mhspec = MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.EXACTLY);
			} else {
				mhspec = MeasureSpec.makeMeasureSpec(Math.round(lp.getHeight()), MeasureSpec.EXACTLY);
			}
			child.measure(mwspec, mhspec);
		}

		setMeasuredDimension(myWidth, myHeight);
	}

	private boolean measureChild(int widthMeasureSpec, int heightMeasureSpec, View child, LayoutParams lp) {
		int dimenW, dimenH;
		if (lp.width == LayoutParams.UNSPECIFIED) {
			float w = lp.getWidth();
			if (w == w) {
				dimenW = Math.round(w);
			} else {
				return false;
			}
		} else {
			dimenW = lp.width;
		}
		if (lp.height == LayoutParams.UNSPECIFIED) {
			float h = lp.getHeight();
			if (h == h) {
				dimenH = Math.round(h);
			} else {
				return false;
			}
		} else {
			dimenH = lp.height;
		}
		int specW = myWidth == -1 ? widthMeasureSpec : MeasureSpec.makeMeasureSpec(myWidth, MeasureSpec.EXACTLY);
		int specH = myHeight == -1 ? heightMeasureSpec : MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.EXACTLY);
		child.measure(getChildMeasureSpec(specW, 0, dimenW), getChildMeasureSpec(specH, 0, dimenH));
		lp.mMeasuredWidth = child.getMeasuredWidth();
		lp.mMeasuredHeight = child.getMeasuredHeight();
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//  The layout has actually already been performed and the positions
		//  cached.  Apply the cached values to the children.
		final int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) child.getLayoutParams();
				child.layout(Math.round(lp.getLeft()), Math.round(lp.getTop()), Math.round(lp.getRight()), Math.round(lp.getBottom()));
			}
		}
	}

	//
	// Parser and Eval
	// Shunting-yard algorithm & RPN
	//

	static abstract class Operator {

		public static final int ASSOC_LEFT = 1;
		public static final int ASSOC_RIGHT = 2;

		public static final int FLAG_FUNCTION = 1;

		public final String op;
		public final int prec;
		public final int assoc;
		public final int argc;
		public final int flag;

		public Operator(String op, int prec, int assoc, int argc, int flag) {
			this.op = op;
			this.prec = prec;
			this.assoc = assoc;
			this.argc = argc;
			this.flag = flag;
		}

		public abstract float eval(FlexLayout fl, int index, int xy, float a, float b);

		@Override
		public String toString() {
			return op;
		}

	}

	static final Operator MUL = new Operator("*", 8, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return a * b;
		}
	};
	static final Operator DIV = new Operator("/", 8, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return a / b;
		}
	};
	static final Operator PERC = new Operator("%", 10, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (xy == 0) {
				if (fl.myWidth == -1) {
					return Float.NaN;
				} else {
					return fl.myWidth * a * 0.01f;
				}
			} else {
				if (fl.myHeight == -1) {
					return Float.NaN;
				} else {
					return fl.myHeight * a * 0.01f;
				}
			}
		}
	};
	static final Operator ADD = new Operator("+", 5, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return a + b;
		}
	};
	static final Operator SUB = new Operator("-", 5, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return a - b;
		}
	};
	static final Operator BL = new Operator("(", 0, 0, 0, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return Float.NaN;
		}
	};
	static final Operator BR = new Operator(")", 0, 0, 0, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return Float.NaN;
		}
	};
	static final Operator COMMA = new Operator(",", 0, Operator.ASSOC_LEFT, 0, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return Float.NaN;
		}
	};
	static final Operator U_SP = new Operator("sp", 10, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, a, fl.getResources().getDisplayMetrics());
		}
	};
	static final Operator U_DP = new Operator("dp", 10, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, fl.getResources().getDisplayMetrics());
		}
	};
	static final Operator U_DIP = new Operator("dip", 10, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, fl.getResources().getDisplayMetrics());
		}
	};
	static final Operator U_PX = new Operator("px", 10, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, a, fl.getResources().getDisplayMetrics());
		}
	};
	static final Operator U_PT = new Operator("pt", 10, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, a, fl.getResources().getDisplayMetrics());
		}
	};
	static final Operator U_MM = new Operator("mm", 10, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, a, fl.getResources().getDisplayMetrics());
		}
	};
	static final Operator U_IN = new Operator("in", 10, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, a, fl.getResources().getDisplayMetrics());
		}
	};
	static final Operator F_MAX = new Operator("max", 0, 0, 2, Operator.FLAG_FUNCTION) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return Math.max(a, b);
		}
	};
	static final Operator F_MIN = new Operator("min", 0, 0, 2, Operator.FLAG_FUNCTION) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return Math.min(a, b);
		}
	};
	static final Operator F_ROUND = new Operator("round", 0, 0, 1, Operator.FLAG_FUNCTION) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return Math.round(a);
		}
	};
	static final Operator F_CEIL = new Operator("ceil", 0, 0, 1, Operator.FLAG_FUNCTION) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return (float) Math.ceil(a);
		}
	};
	static final Operator F_FLOOR = new Operator("floor", 0, 0, 1, Operator.FLAG_FUNCTION) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return (float) Math.floor(a);
		}
	};
	static final Operator F_ABS = new Operator("abs", 0, 0, 1, Operator.FLAG_FUNCTION) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return Math.abs(a);
		}
	};
	static final Operator F_MOD = new Operator("mod", 0, 0, 2, Operator.FLAG_FUNCTION) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return a % b;
		}
	};
	static final Operator F_POW = new Operator("pow", 0, 0, 2, Operator.FLAG_FUNCTION) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return (float) Math.pow(a, b);
		}
	};

	static Operator[] OPS = new Operator[]{ADD, SUB, DIV, MUL, PERC, BL, BR, COMMA,
			U_SP, U_DP, U_DIP, U_PX, U_PT, U_MM, U_IN,
			F_MAX, F_MIN, F_ROUND, F_CEIL, F_FLOOR, F_ABS, F_MOD, F_POW};

	static class Ref {

		public static final int TARGET_THIS = 0;
		public static final int TARGET_PREV = 1;
		public static final int TARGET_NEXT = 2;
		public static final int TARGET_PARENT = 3;
		public static final int TARGET_SCREEN = 4;

		public static final int PROP_LEFT = 0;
		public static final int PROP_TOP = 1;
		public static final int PROP_RIGHT = 2;
		public static final int PROP_BOTTOM = 3;
		public static final int PROP_CENTER_X = 4;
		public static final int PROP_CENTER_Y = 5;
		public static final int PROP_WIDTH = 6;
		public static final int PROP_HEIGHT = 7;

		public final int target;
		public final int property;

		public Ref(int target, int prop) {
			this.target = target;
			this.property = prop;
		}

		@Override
		public String toString() {
			return target + "->" + property;
		}

		public float eval(FlexLayout fl, int index, int xy) {
			View view = null;
			if (target == TARGET_THIS) {
				view = fl.getChildAt(index);
			} else if (target == TARGET_PREV) {
				view = index > 0 ? fl.getChildAt(index - 1) : null;
			} else if (target == TARGET_NEXT) {
				view = index < fl.getChildCount() - 1 ? fl.getChildAt(index + 1) : null;
			} else if (target == TARGET_PARENT) {
				if (property == PROP_WIDTH) {
					if (fl.myWidth == -1) {
						return Float.NaN;
					} else {
						return fl.myWidth;
					}
				} else if (property == PROP_HEIGHT) {
					if (fl.myHeight == -1) {
						return Float.NaN;
					} else {
						return fl.myHeight;
					}
				} else {
					throw new RuntimeException("only support parent.width and parent.height");
				}
			} else if (target == TARGET_SCREEN) {
				DisplayMetrics dm = fl.getResources().getDisplayMetrics();
				if (property == PROP_WIDTH) {
					return dm.widthPixels;
				} else if (property == PROP_HEIGHT) {
					return dm.heightPixels;
				} else {
					throw new RuntimeException("only support screen.width and screen.height");
				}
			} else {
				for (int i = 0, n = fl.getChildCount(); i < n; i++) {
					View v = fl.getChildAt(i);
					if (v.getId() == target) {
						view = v;
						break;
					}
				}
				if (view == null) {
					// TODO:
					throw new RuntimeException("view not found");
				}
			}
			if (view == null) {
				return 0;
			}
			FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
			if (property == PROP_LEFT) {
				return lp.getLeft();
			} else if (property == PROP_TOP) {
				return lp.getTop();
			} else if (property == PROP_RIGHT) {
				return lp.getRight();
			} else if (property == PROP_BOTTOM) {
				return lp.getBottom();
			} else if (property == PROP_CENTER_X) {
				return lp.getCenterX();
			} else if (property == PROP_CENTER_Y) {
				return lp.getCenterY();
			} else if (property == PROP_WIDTH) {
				return lp.getWidth();
			} else if (property == PROP_HEIGHT) {
				return lp.getHeight();
			}
			return Float.NaN;
		}

	}

	static class TokenReader {

		private char[] chars;
		private int n;
		private int i;

		public TokenReader(String str) {
			chars = str.toCharArray();
			n = str.length();
			i = 0;
		}

		/**
		 * Number, Operator, Ref
		 */
		public Object readToken(Context ctx) {
			StringBuilder num = null; // 1.32
			StringBuilder dimen = null; // @dimen/xxx
			StringBuilder str = null; // func, ref, etc (_|a-Z|0-9)
			int strDig = -1;
			int dimenSlash = -1;
			while (i < n) {
				char c = chars[i];
				if (num == null && dimen == null && str == null) {
					if (c >= '0' && c <= '9' || c == '.') {
						num = new StringBuilder();
						num.append(c);
					} else if (c == ' ' || c == '\t') {
					} else if (c == '@') {
						dimen = new StringBuilder();
						dimen.append(c);
					} else if (c >= 'a' && c <= 'z' || c == '_' || c >= 'A' && c <= 'Z') {
						str = new StringBuilder();
						str.append(c);
					} else {
						for (Operator op : OPS) {
							if (op.op.length() == 1 && op.op.charAt(0) == c) {
								i++;
								return op;
							}
						}
						throw new RuntimeException("syntax error");
					}
				} else if (num != null) {
					if (c >= '0' && c <= '9' || c == '.') {
						num.append(c);
					} else {
						return Float.parseFloat(num.toString());
					}
				} else if (dimen != null) {
					if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c >= 'A' && c <= 'Z') {
						dimen.append(c);
					} else if (c == '/' && dimenSlash == -1) {
						dimenSlash = dimen.length();
						dimen.append(c);
					} else {
						return parseDimen(ctx, dimen, dimenSlash);
					}
				} else {
					if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c >= 'A' && c <= 'Z') {
						str.append(c);
					} else if (c == '.') {
						strDig = str.length();
						str.append(c);
					} else {
						return parseStr(ctx, str, strDig);
					}
				}
				i++;
			}
			if (num != null) {
				return Float.parseFloat(num.toString());
			}
			if (dimen != null) {
				return parseDimen(ctx, dimen, dimenSlash);
			}
			if (str != null) {
				return parseStr(ctx, str, strDig);
			}
			return null;
		}

		private Object parseStr(Context ctx, StringBuilder str, int strDig) {
			if (strDig == -1) {
				String s = str.toString();
				for (Operator op : OPS) {
					if (op.op.equals(s)) {
						return op;
					}
				}
				throw new RuntimeException("unknown token " + s);
			} else {
				String s1 = str.substring(0, strDig);
				String s2 = str.substring(strDig + 1);
				int refT;
				if ("this".equals(s1)) {
					refT = Ref.TARGET_THIS;
				} else if ("prev".equals(s1)) {
					refT = Ref.TARGET_PREV;
				} else if ("next".equals(s1)) {
					refT = Ref.TARGET_NEXT;
				} else if ("parent".equals(s1)) {
					refT = Ref.TARGET_PARENT;
				} else if ("screen".equals(s1)) {
					refT = Ref.TARGET_SCREEN;
				} else {
					// TODO: android:text1
					int id = ctx.getResources().getIdentifier(s1, "id", ctx.getPackageName());
					if (id == 0) {
						throw new RuntimeException("unknown identifier " + s1);
					} else {
						refT = id;
					}
				}
				int refP;
				if ("left".equals(s2)) {
					refP = Ref.PROP_LEFT;
				} else if ("top".equals(s2)) {
					refP = Ref.PROP_TOP;
				} else if ("right".equals(s2)) {
					refP = Ref.PROP_RIGHT;
				} else if ("bottom".equals(s2)) {
					refP = Ref.PROP_BOTTOM;
				} else if ("centerX".equals(s2)) {
					refP = Ref.PROP_CENTER_X;
				} else if ("centerY".equals(s2)) {
					refP = Ref.PROP_CENTER_Y;
				} else if ("width".equals(s2)) {
					refP = Ref.PROP_WIDTH;
				} else if ("height".equals(s2)) {
					refP = Ref.PROP_HEIGHT;
				} else {
					throw new RuntimeException("unknown token " + s2);
				}
				return new Ref(refT, refP);
			}
		}

		private float parseDimen(Context ctx, StringBuilder dimen, int dimenSlash) {
			if (dimenSlash == -1) {
				throw new RuntimeException("unknown token " + dimen);
			} else {
				String s1 = dimen.substring(1, dimenSlash);
				String s2 = dimen.substring(dimenSlash + 1);
				String pn;
				if ("dimen".equals(s1)) {
					pn = ctx.getPackageName();
				} else if ("android:dimen".equals(s1)) {
					pn = "android";
				} else {
					throw new RuntimeException("unknown identifier " + dimen);
				}
				int id = ctx.getResources().getIdentifier(s2, "dimen", pn);
				if (id == 0) {
					throw new RuntimeException("unknown identifier " + dimen);
				}
				return ctx.getResources().getDimension(id);
			}
		}

		public void reset() {
			i = 0;
		}

	}

	static class RPN {

		private ArrayList<Object> list;

		public RPN(ArrayList<Object> list) {
			this.list = list;
		}

		/**
		 * Shunting-yard algorithm
		 */
		public static RPN parse(Context ctx, String str) {
			if (str == null || str.length() == 0) {
				return null;
			}

			TokenReader tr = new TokenReader(str);
			ArrayList<Object> queue = new ArrayList<>();
			Stack<Operator> stack = new Stack<>();

			Object t;
			while ((t = tr.readToken(ctx)) != null) {
				if (t instanceof Number) {
					queue.add(t);
				} else if (t instanceof Ref) {
					queue.add(t);
				} else if (t instanceof Operator) {
					Operator op = (Operator) t;
					if ((op.flag & Operator.FLAG_FUNCTION) != 0) {
						stack.push(op);
					} else if (op == COMMA) {
						while (!stack.empty() && stack.peek() != BL) {
							queue.add(stack.pop());
						}
						if (stack.empty()) {
							throw new RuntimeException("comma misplaced or parentheses mismatched");
						}
					} else if (op == BL) {
						stack.push(op);
					} else if (op == BR) {
						while (!stack.empty() && stack.peek() != BL) {
							queue.add(stack.pop());
						}
						if (stack.empty()) {
							throw new RuntimeException("parentheses mismatched");
						}
						stack.pop();
						if (!stack.empty() && (stack.peek().flag & Operator.FLAG_FUNCTION) != 0) {
							queue.add(stack.pop());
						}
					} else {
						if (!stack.empty()) {
							Operator o2 = stack.peek();
							if (op.assoc == Operator.ASSOC_LEFT && op.prec <= o2.prec
									|| op.assoc == Operator.ASSOC_RIGHT && op.prec < o2.prec) {
								queue.add(stack.pop());
							}
						}
						stack.push(op);
					}
				} else {
					throw new RuntimeException("unknown token " + t);
				}
			}

			while (!stack.empty()) {
				Operator op = stack.pop();
				if (op == BL) {
					throw new RuntimeException("parentheses mismatched");
				} else if (op.assoc == 0) {
					throw new RuntimeException("syntax error");
				} else {
					queue.add(op);
				}
			}

			if (queue.isEmpty()) {
				return null;
			} else {
				return new RPN(queue);
			}
		}

		public float eval(FlexLayout fl, int index, int xy) {
			float[] stack = new float[list.size()];
			int sn = 0;

			for (Object obj : list) {
				if (obj instanceof Operator) {
					Operator op = (Operator) obj;
					if (sn < op.argc) {
						throw new RuntimeException("arg error " + op);
					}
					float a = Float.NaN, b = Float.NaN;
					if (op.argc == 0) {
					} else if (op.argc == 1) {
						a = stack[--sn];
					} else if (op.argc == 2) {
						b = stack[--sn];
						a = stack[--sn];
					} else {
						throw new RuntimeException("argc>2 not supported");
					}
					float c = op.eval(fl, index, xy, a, b);
					stack[sn++] = c;
				} else if (obj instanceof Float) {
					stack[sn++] = ((Float) obj).floatValue();
				} else if (obj instanceof Ref) {
					float f = ((Ref) obj).eval(fl, index, xy);
					stack[sn++] = f;
				} else {
					throw new RuntimeException("unknown token " + obj);
				}
			}

			if (sn != 1) {
				throw new RuntimeException("syntax error");
			}
			return stack[0];
		}

		@Override
		public String toString() {
			return String.valueOf(list);
		}
	}

}
