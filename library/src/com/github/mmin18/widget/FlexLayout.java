package com.github.mmin18.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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
 * layout_left, layout_right, layout_top, layout_bottom, layout_centerX, layout_centerY, layout_width, layout_height
 * wrap_content, match_parent, 100%, 50%+100dp
 * this.width*1.6, prev.right+10dp, next.left-10dp
 * parent.height/2 (only parent.width and parent.height are supported)
 * other_view.width*0.5-30px (other_view is defined as res id)
 * screen.width, screen.height (screen width/height is relative to current orientation)
 * max(view1.right,view2.right), min(), round(), ceil(), floor(), abs(), mod(), pow()
 * prev.visible, prev.gone, prev.tag
 * a==b, a!=b, a<=b, a<b, a>=b, a>b
 * a&&b, a||b
 * a?b:c
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

		String positionDescription; // only available in debug mode

		static final int[] ViewGroup_Layout = new int[]{android.R.attr.layout_width, android.R.attr.layout_height};

		public LayoutParams(Context c, AttributeSet attrs) {
			super(0, 0);

			if (isDebug(c)) {
				positionDescription = attrs.getPositionDescription();
			}

			TypedArray a = c.obtainStyledAttributes(attrs, ViewGroup_Layout);
			width = a.getLayoutDimension(0, UNSPECIFIED);
			height = a.getLayoutDimension(1, UNSPECIFIED);
			a.recycle();

			a = c.obtainStyledAttributes(attrs, R.styleable.FlexLayout_Layout);
			this.left = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_left), "layout_left");
			this.top = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_top), "layout_top");
			this.right = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_right), "layout_right");
			this.bottom = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_bottom), "layout_bottom");
			this.centerX = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_centerX), "layout_centerX");
			this.centerY = RPN.parse(c, a.getString(R.styleable.FlexLayout_Layout_layout_centerY), "layout_centerY");
			String str = a.getString(R.styleable.FlexLayout_Layout_layout_width);
			if ("match_parent".equals(str) || "fill_parent".equals(str)) {
				width = MATCH_PARENT;
			} else if ("wrap_content".equals(str)) {
				width = WRAP_CONTENT;
			} else {
				this.width2 = RPN.parse(c, str, "layout_width");
			}
			str = a.getString(R.styleable.FlexLayout_Layout_layout_height);
			if ("match_parent".equals(str) || "fill_parent".equals(str)) {
				height = MATCH_PARENT;
			} else if ("wrap_content".equals(str)) {
				height = WRAP_CONTENT;
			} else {
				this.height2 = RPN.parse(c, str, "layout_height");
			}
			a.recycle();

			int co = 0;
			if (this.left != null)
				co++;
			if (this.right != null)
				co++;
			if (this.centerX != null)
				co++;
			if (this.width2 != null || this.width != LayoutParams.UNSPECIFIED)
				co++;
			if (co < 1) {
				throw new IllegalArgumentException("no LayoutParams in layout_left|layout_right|layout_centerX|layout_width");
			}
			co = 0;
			if (this.top != null)
				co++;
			if (this.bottom != null)
				co++;
			if (this.centerY != null)
				co++;
			if (this.height2 != null || this.height != LayoutParams.UNSPECIFIED)
				co++;
			if (co < 1) {
				throw new IllegalArgumentException("no LayoutParams in layout_top|layout_bottom|layout_centerY|layout_height");
			}
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

		final int paddingLeft = getPaddingLeft();
		final int paddingRight = getPaddingRight();
		final int paddingTop = getPaddingTop();
		final int paddingBottom = getPaddingBottom();

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int maxWidth, maxHeight;
		if (widthMode == MeasureSpec.EXACTLY) {
			myWidth = maxWidth = (widthSize - paddingLeft - paddingRight);
		} else if (widthMode == MeasureSpec.AT_MOST) {
			myWidth = -1;
			maxWidth = (widthSize - paddingLeft - paddingRight);
		} else {
			myWidth = -1;
			maxWidth = -1;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			myHeight = maxHeight = (heightSize - paddingTop - paddingBottom);
		} else if (heightMode == MeasureSpec.AT_MOST) {
			myHeight = -1;
			maxHeight = (heightSize - paddingTop - paddingBottom);
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
					float v = lp.left.eval(this, i, 0, lp.positionDescription);
					if (v == v) {
						lp.mLeft = v;
						calcCount++;
					}
				}
				if (lp.right != null && lp.mRight != lp.mRight) {
					float v = lp.right.eval(this, i, 0, lp.positionDescription);
					if (v == v) {
						lp.mRight = v;
						calcCount++;
					}
				}
				if (lp.top != null && lp.mTop != lp.mTop) {
					float v = lp.top.eval(this, i, 1, lp.positionDescription);
					if (v == v) {
						lp.mTop = v;
						calcCount++;
					}
				}
				if (lp.bottom != null && lp.mBottom != lp.mBottom) {
					float v = lp.bottom.eval(this, i, 1, lp.positionDescription);
					if (v == v) {
						lp.mBottom = v;
						calcCount++;
					}
				}
				if (lp.centerX != null && lp.mCenterX != lp.mCenterX) {
					float v = lp.centerX.eval(this, i, 0, lp.positionDescription);
					if (v == v) {
						lp.mCenterX = v;
						calcCount++;
					}
				}
				if (lp.centerY != null && lp.mCenterY != lp.mCenterY) {
					float v = lp.centerY.eval(this, i, 1, lp.positionDescription);
					if (v == v) {
						lp.mCenterY = v;
						calcCount++;
					}
				}
				if (lp.mWidth != lp.mWidth) {
					if (lp.width2 != null) {
						float v = lp.width2.eval(this, i, 0, lp.positionDescription);
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
						float v = lp.height2.eval(this, i, 1, lp.positionDescription);
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
					throw new IllegalStateException("incomplete layout, circular dependency? (index=" + invIndex + ")");
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
			throw new IllegalStateException("incomplete layout, circular dependency? (index=" + sb + ")");
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

		setMeasuredDimension(myWidth + paddingLeft + paddingRight, myHeight + paddingTop + paddingBottom);
	}

	private boolean measureChild(int widthMeasureSpec, int heightMeasureSpec, View child, LayoutParams lp) {
		int dimenW, dimenH;
		if (lp.width == LayoutParams.UNSPECIFIED) {
			float w = lp.getWidth();
			if (w == w) {
				dimenW = Math.round(w);
			} else if (onlyRefSelf(lp.width2) && onlyRefSelf(lp.left) && onlyRefSelf(lp.right) && onlyRefSelf(lp.centerX)) {
				// in case layout_width="this.height"
				dimenW = ViewGroup.LayoutParams.WRAP_CONTENT;
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
			} else if (onlyRefSelf(lp.height2) && onlyRefSelf(lp.top) && onlyRefSelf(lp.bottom) && onlyRefSelf(lp.centerY)) {
				// in case layout_height="this.width"
				dimenH = ViewGroup.LayoutParams.WRAP_CONTENT;
			} else {
				return false;
			}
		} else {
			dimenH = lp.height;
		}
		int specW;
		if (myWidth == -1) {
			specW = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), dimenW);
		} else {
			specW = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(myWidth, MeasureSpec.EXACTLY), 0, dimenW);
		}
		int specH;
		if (myHeight == -1) {
			specH = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), dimenH);
		} else {
			specH = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.EXACTLY), 0, dimenH);
		}
		child.measure(specW, specH);
		lp.mMeasuredWidth = child.getMeasuredWidth();
		lp.mMeasuredHeight = child.getMeasuredHeight();
		return true;
	}

	private boolean onlyRefSelf(RPN exp) {
		if (exp != null) {
			for (Object obj : exp.list) {
				if (obj instanceof Ref) {
					Ref ref = (Ref) obj;
					if (ref.target != Ref.TARGET_THIS) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int paddingLeft = getPaddingLeft();
		final int paddingTop = getPaddingTop();

		//  The layout has actually already been performed and the positions
		//  cached.  Apply the cached values to the children.
		final int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) child.getLayoutParams();
				child.layout(paddingLeft + Math.round(lp.getLeft()), paddingTop + Math.round(lp.getTop()),
						paddingLeft + Math.round(lp.getRight()), paddingTop + Math.round(lp.getBottom()));
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

	// Precedence
	// () sp dp dip px pt mm in
	// !
	// * / %
	// + -
	// < <= > >=
	// == !=
	// &&
	// ||
	// ?:

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
	static final Operator PERC = new Operator("%", 8, Operator.ASSOC_RIGHT, 1, 0) {
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
	static final Operator ADD = new Operator("+", 7, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return a + b;
		}
	};
	static final Operator SUB = new Operator("-", 7, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return a - b;
		}
	};
	static final Operator NOT = new Operator("!", 9, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a) {
				return a == 0 ? 1 : 0;
			} else {
				return Float.NaN;
			}
		}
	};
	static final Operator CP_LT = new Operator("<", 6, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a && b == b) {
				return a < b ? 1 : 0;
			} else {
				return Float.NaN;
			}
		}
	};
	static final Operator CP_LT_EQ = new Operator("<=", 6, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a && b == b) {
				return a <= b ? 1 : 0;
			} else {
				return Float.NaN;
			}
		}
	};
	static final Operator CP_GT = new Operator(">", 6, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a && b == b) {
				return a > b ? 1 : 0;
			} else {
				return Float.NaN;
			}
		}
	};
	static final Operator CP_GT_EQ = new Operator(">=", 6, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a && b == b) {
				return a >= b ? 1 : 0;
			} else {
				return Float.NaN;
			}
		}
	};
	static final Operator CP_EQ = new Operator("==", 5, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a && b == b) {
				return a == b ? 1 : 0;
			} else {
				return Float.NaN;
			}
		}
	};
	static final Operator CP_NOT_EQ = new Operator("!=", 5, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a && b == b) {
				return a != b ? 1 : 0;
			} else {
				return Float.NaN;
			}
		}
	};
	static final Operator LOG_AND = new Operator("&&", 4, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a && b == b) {
				return a != 0 && b != 0 ? 1 : 0;
			} else {
				return Float.NaN;
			}
		}
	};
	static final Operator LOG_OR = new Operator("||", 3, Operator.ASSOC_LEFT, 2, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			if (a == a && b == b) {
				return a != 0 || b != 0 ? 1 : 0;
			} else {
				return Float.NaN;
			}
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
	public static final Operator X_COND1 = new Operator("?", 2, Operator.ASSOC_RIGHT, 1, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return a;
		}
	};
	public static final Operator X_COND2 = new Operator(":", 1, Operator.ASSOC_LEFT, 3, 0) {
		@Override
		public float eval(FlexLayout fl, int index, int xy, float a, float b) {
			return Float.NaN;
		}
	};

	static Operator[] OPS = new Operator[]{ADD, SUB, DIV, MUL, PERC, NOT,
			CP_LT, CP_LT_EQ, CP_GT, CP_GT_EQ, CP_EQ, CP_NOT_EQ, LOG_AND, LOG_OR,
			BL, BR, COMMA,
			U_SP, U_DP, U_DIP, U_PX, U_PT, U_MM, U_IN,
			F_MAX, F_MIN, F_ROUND, F_CEIL, F_FLOOR, F_ABS, F_MOD, F_POW,
			X_COND1, X_COND2};

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
		public static final int PROP_VISIBLE = 10;
		public static final int PROP_GONE = 11;
		public static final int PROP_TAG = 15;

		public final int target;
		public final int property;

		public Ref(int target, int prop) {
			this.target = target;
			this.property = prop;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			switch (target) {
				case TARGET_THIS:
					sb.append("this");
					break;
				case TARGET_PREV:
					sb.append("prev");
					break;
				case TARGET_NEXT:
					sb.append("next");
					break;
				case TARGET_PARENT:
					sb.append("parent");
					break;
				case TARGET_SCREEN:
					sb.append("screen");
					break;
				default:
					sb.append("?");
					break;
			}
			sb.append('.');
			switch (property) {
				case PROP_LEFT:
					sb.append("left");
					break;
				case PROP_TOP:
					sb.append("top");
					break;
				case PROP_RIGHT:
					sb.append("right");
					break;
				case PROP_BOTTOM:
					sb.append("bottom");
					break;
				case PROP_CENTER_X:
					sb.append("centerX");
					break;
				case PROP_CENTER_Y:
					sb.append("centerY");
					break;
				case PROP_WIDTH:
					sb.append("width");
					break;
				case PROP_HEIGHT:
					sb.append("height");
					break;
				case PROP_VISIBLE:
					sb.append("visible");
					break;
				case PROP_GONE:
					sb.append("gone");
					break;
				case PROP_TAG:
					sb.append("tag");
					break;
				default:
					sb.append("?");
					break;
			}
			return sb.toString();
		}

		public float eval(FlexLayout fl, int index, int xy, String positionDescription) {
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
				} else if (property == PROP_LEFT || property == PROP_TOP || property == PROP_RIGHT || property == PROP_BOTTOM || property == PROP_CENTER_X || property == PROP_CENTER_Y) {
					throw new IllegalArgumentException(this.toString() + " is not supported" + (positionDescription == null ? "" : " (" + positionDescription + ")"));
				} else {
					view = fl;
				}
			} else if (target == TARGET_SCREEN) {
				DisplayMetrics dm = fl.getResources().getDisplayMetrics();
				if (property == PROP_WIDTH) {
					return dm.widthPixels;
				} else if (property == PROP_HEIGHT) {
					return dm.heightPixels;
				} else {
					throw new IllegalArgumentException(this.toString() + " is not supported" + (positionDescription == null ? "" : " (" + positionDescription + ")"));
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
					throw new IllegalArgumentException("view not found" + (positionDescription == null ? "" : " (" + positionDescription + ")"));
				}
			}
			if (view == null) {
				return 0;
			}
			if (property == PROP_LEFT) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
				return lp.getLeft();
			} else if (property == PROP_TOP) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
				return lp.getTop();
			} else if (property == PROP_RIGHT) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
				return lp.getRight();
			} else if (property == PROP_BOTTOM) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
				return lp.getBottom();
			} else if (property == PROP_CENTER_X) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
				return lp.getCenterX();
			} else if (property == PROP_CENTER_Y) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
				return lp.getCenterY();
			} else if (property == PROP_WIDTH) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
				return lp.getWidth();
			} else if (property == PROP_HEIGHT) {
				FlexLayout.LayoutParams lp = (FlexLayout.LayoutParams) view.getLayoutParams();
				return lp.getHeight();
			} else if (property == PROP_VISIBLE) {
				return view.getVisibility() == View.VISIBLE ? 1 : 0;
			} else if (property == PROP_GONE) {
				return view.getVisibility() == View.GONE ? 1 : 0;
			} else if (property == PROP_TAG) {
				Object tag = view.getTag();
				if (tag instanceof Number) {
					return ((Number) tag).floatValue();
				} else if (tag instanceof Boolean) {
					return ((Boolean) tag).booleanValue() ? 1 : 0;
				} else {
					return 0;
				}
			} else {
				return Float.NaN;
			}
		}

	}

	static class TokenReader {

		private String orig;
		private char[] chars;
		private int n;
		private int i;
		private String from;

		public TokenReader(String str, String from) {
			orig = str;
			chars = str.toCharArray();
			n = str.length();
			i = 0;
			this.from = from;
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
						// ==, !=, <=, >=, &&, ||
						char nc = i + 1 < n ? chars[i + 1] : 0;
						if (nc == '=') {
							if (c == '=') {
								i += 2;
								return CP_EQ;
							} else if (c == '!') {
								i += 2;
								return CP_NOT_EQ;
							} else if (c == '<') {
								i += 2;
								return CP_LT_EQ;
							} else if (c == '>') {
								i += 2;
								return CP_GT_EQ;
							}
						} else if (c == '&' && nc == '&') {
							i += 2;
							return LOG_AND;
						} else if (c == '|' && nc == '|') {
							i += 2;
							return LOG_OR;
						}
						for (Operator op : OPS) {
							if (op.op.length() == 1 && op.op.charAt(0) == c) {
								i++;
								return op;
							}
						}
						throw new IllegalArgumentException("syntax error: " + from + "=" + orig);
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
					} else if (c == ':' && "@android".equals(dimen.toString())) {
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
					} else if (c == ':' && "android".equals(str.toString())) {
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
				throw new IllegalArgumentException("unknown token " + s + ", " + from + "=" + orig);
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
					int id;
					if (s1.startsWith("android:")) {
						id = ctx.getResources().getIdentifier(s1.substring("android:".length()), "id", "android");
					} else {
						id = ctx.getResources().getIdentifier(s1, "id", ctx.getPackageName());
					}
					if (id == 0) {
						throw new IllegalArgumentException("unknown identifier " + s1 + ", " + from + "=" + orig);
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
				} else if ("visible".equals(s2)) {
					refP = Ref.PROP_VISIBLE;
				} else if ("gone".equals(s2)) {
					refP = Ref.PROP_GONE;
				} else if ("tag".equals(s2)) {
					refP = Ref.PROP_TAG;
				} else {
					throw new IllegalArgumentException("unknown token " + s2 + ", " + from + "=" + orig);
				}
				return new Ref(refT, refP);
			}
		}

		private float parseDimen(Context ctx, StringBuilder dimen, int dimenSlash) {
			if (dimenSlash == -1) {
				throw new IllegalArgumentException("unknown token " + dimen + ", " + from + "=" + orig);
			} else {
				String s1 = dimen.substring(1, dimenSlash);
				String s2 = dimen.substring(dimenSlash + 1);
				String pn;
				if ("dimen".equals(s1)) {
					pn = ctx.getPackageName();
				} else if ("android:dimen".equals(s1)) {
					pn = "android";
				} else {
					throw new IllegalArgumentException("unknown identifier " + dimen + ", " + from + "=" + orig);
				}
				int id = ctx.getResources().getIdentifier(s2, "dimen", pn);
				if (id == 0) {
					throw new IllegalArgumentException("unknown identifier " + dimen + ", " + from + "=" + orig);
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
		private String orig; // only available in debug mode

		public RPN(ArrayList<Object> list, String orig) {
			this.list = list;
			this.orig = orig;
		}

		/**
		 * Shunting-yard algorithm
		 */
		public static RPN parse(Context ctx, String str, String from) {
			if (str == null || str.length() == 0) {
				return null;
			}

			TokenReader tr = new TokenReader(str, from);
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
							throw new IllegalArgumentException("comma misplaced or parentheses mismatched: " + from + "=" + str);
						}
					} else if (op == BL) {
						stack.push(op);
					} else if (op == BR) {
						while (!stack.empty() && stack.peek() != BL) {
							queue.add(stack.pop());
						}
						if (stack.empty()) {
							throw new IllegalArgumentException("parentheses mismatched: " + from + "=" + str);
						}
						stack.pop();
						if (!stack.empty() && (stack.peek().flag & Operator.FLAG_FUNCTION) != 0) {
							queue.add(stack.pop());
						}
					} else {
						while (!stack.empty()) {
							Operator o2 = stack.peek();
							if (op.assoc == Operator.ASSOC_LEFT && op.prec <= o2.prec
									|| op.assoc == Operator.ASSOC_RIGHT && op.prec < o2.prec) {
								queue.add(stack.pop());
							} else {
								break;
							}
						}
						stack.push(op);
					}
				} else {
					throw new IllegalArgumentException("unknown token " + t + ", " + from + "=" + str);
				}
			}

			while (!stack.empty()) {
				Operator op = stack.pop();
				if (op == BL) {
					throw new IllegalArgumentException("parentheses mismatched: " + from + "=" + str);
				} else if (op.assoc == 0) {
					throw new IllegalArgumentException("syntax error: " + from + "=" + str);
				} else {
					queue.add(op);
				}
			}

			if (queue.isEmpty()) {
				return null;
			} else {
				return new RPN(queue, isDebug(null) ? from + "=" + str : null);
			}
		}

		public float eval(FlexLayout fl, int index, int xy, String positionDescription) {
			float[] stack = new float[list.size()];
			int sn = 0;

			for (Object obj : list) {
				if (obj instanceof Operator) {
					Operator op = (Operator) obj;
					if (sn < op.argc) {
						throw new IllegalArgumentException("arg error " + op
								+ (positionDescription == null || orig == null ? "" : " (" + positionDescription + ":" + orig + ")"));
					}
					float a = Float.NaN, b = Float.NaN;
					if (op.argc == 0) {
					} else if (op.argc == 1) {
						a = stack[--sn];
					} else if (op.argc == 2) {
						b = stack[--sn];
						a = stack[--sn];
					} else {
						if (op == X_COND2) {
							// a?b:c special case, maybe a better solution?
							b = stack[--sn];
							a = stack[--sn];
							float cond = stack[--sn];
							float c = cond == cond ? (cond != 0 ? a : b) : Float.NaN;
							stack[sn++] = c;
							continue;
						}
						throw new IllegalArgumentException("argc>2 not supported"
								+ (positionDescription == null || orig == null ? "" : " (" + positionDescription + ":" + orig + ")"));
					}
					float c = op.eval(fl, index, xy, a, b);
					stack[sn++] = c;
				} else if (obj instanceof Float) {
					stack[sn++] = ((Float) obj).floatValue();
				} else if (obj instanceof Ref) {
					float f = ((Ref) obj).eval(fl, index, xy,
							positionDescription == null || orig == null ? null : positionDescription + ":" + orig);
					stack[sn++] = f;
				} else {
					throw new IllegalArgumentException("unknown token " + obj
							+ (positionDescription == null || orig == null ? "" : " (" + positionDescription + ":" + orig + ")"));
				}
			}

			if (sn != 1) {
				throw new IllegalArgumentException("syntax error"
						+ (positionDescription == null || orig == null ? "" : " (" + positionDescription + ":" + orig + ")"));
			}
			return stack[0];
		}

		@Override
		public String toString() {
			return String.valueOf(list);
		}
	}

	// android:debuggable="true" in AndroidManifest.xml (auto set by build tool)
	static Boolean DEBUG = null;

	static boolean isDebug(Context ctx) {
		if (DEBUG == null && ctx != null) {
			DEBUG = (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		}
		return DEBUG == Boolean.TRUE;
	}
}
