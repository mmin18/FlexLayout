# FlexLayout

The idea is simple, use java expressions in layout params like `layout_left="view1.right+10dp"`. It is helpful when LinearLayout and RelativeLayout is not enough for you.

![IMG](imgs/s1.png)

	<TextView
		app:layout_left="icon.right+10dp"
		app:layout_right="100%-14dp"
		app:layout_centerY="icon.centerY"
		android:layout_height="wrap_content"
		.../>

Try the sample apk: [FlexLayout.apk](imgs/FlexLayout.apk)

## Adding to project

Add dependencies in your `build.gradle`:

```groovy
	dependencies {
	    compile 'com.github.mmin18:flexlayout:1.2.1'
	}
```

Or if you are using Eclipse, just copy [FlexLayout.java](library/src/com/github/mmin18/widget/FlexLayout.java) and [attrs.xml](library/res/values/attrs.xml) to your project.

## Layout Params

|   Horizontal   |    Vertical    |
| -------------- | -------------- |
| layout_left    | layout_top     |
| layout_right   | layout_bottom  |
| layout_centerX | layout_centerY |
| layout_width   | layout_height  |

Remember the `app:layout_width` is different from `android:layout_width`<br>*xmlns:app="http://schemas.android.com/apk/res-auto"*

## % Percentage

![IMG](imgs/s3.png)

	<Button
		app:layout_left="10%"
		app:layout_right="10%"
		app:centerY="50%"
		android:layout_height="wrap_content"
		../>

or

	<Button
		app:layout_width="80%"
		app:centerX="50%"
		app:centerY="50%"
		android:layout_height="wrap_content"
		../>

## Reference other views

Reference previous view using `prev`, next view using `next` (Position in the XML layout file)

![IMG](imgs/s4.png)

	<View ../>        // prev = Previous view in xml layout file
	
	<View
		app:layout_left="prev.right"
		app:layout_right="next.left"
		app:layout_top="prev.top"
		app:layout_bottom="next.bottom" />
	
	<View ../>        // next = Next view in xml layout file

Reference a specific view using `view's id`

![IMG](imgs/s5.png)

	<View
		app:layout_left="view1.right"
		app:layout_right="android:text1.left"
		app:layout_top="view1.top"
		app:layout_bottom="android:text1.bottom" />
	
	<View android:id="@+id/view1"
		../>
	<View android:id="@android:id/text1"
		../>

You can also use `parent` to reference the FlexLayout and `this` to reference the child view itself. Use `screen` to reference screen size.

| Keyword    | Target     |
| ---------- | ---------- |
| prev       | Previous view in XML layout |
| next       | Next view in XML layout |
| *view_id*  | *&lt;View id="@+id/view_id" /&gt;* defined in the same layout |
| this       | The view itself |
| parent     | The parent FlexLayout, doesn't support *left* *top* *right* *bottom* *centerX* *centerY* |
| screen     | Screen size (getResources().getDisplayMetrics(), only support *width* and *height*)|

| Properties |            | Value |
| ---------- | ---------- | ----- |
| left       | top        | |
| right      | bottom     | |
| centerX    | centerY    | |
| width      | height     | |
| visible    |            | view.getVisibility() == View.VISIBLE |
| gone       |            | view.getVisibility() == View.GONE |
| tag        |            | view.getTag(), only support Number or Boolean. Other types or null returns 0 |

(When use with `view.tag`, after View.setTag() you should call View.requestLayout() to trigger layout.)

## Expression

The syntax is the same as Java or C. Numbers can have units like `10dp`, `15sp`

	(parent.height-view1.centerY)/2
	100%-80dp
	max(view1.right, view2.right)
	screen.width<screen.height ? 64dp : 48dp
	view1.visible && view2.visible ? max(view1.bottom, view2.bottom) : 0px

Operators (Order in precedence)

| Operator    | Associativity |
| ----------- |:-------------:|
| () sp dp dip px pt mm in | Right |
| !           | Right         |
| * / %       | Left          |
| + -         | Left          |
| <= < >= >   | Left          |
| == !=       | Left          |
| &&          | Left          |
| ll          | Left          |
| ?=          | Right         |

Functions

| Name        |
| ----------- |
| max(a,b)    |
| min(a,b)    |
| round(a)    |
| ceil(a)     |
| floor(a)    |
| abs(a)      |
| mod(a)      |
| pow(a)      |

## dimens.xml

Of course you can reference dimensions defined in `res/values/dimens.xml`

	<View
		app:left="@dimen/default_margin"
		app:bottom="50%-@dimen/default_margin"
		app:width="2*@android:dimen/app_icon_size"
		../>

# Benchmark

The following benchmark is done by Piasy, and you can check the details [here](http://blog.piasy.com/2016/04/07/Layout-Perf/).

### Simple Layout

|                | inflate (ns) | measure (ns) | layout (ns) |
| -------------- | ------------ | ------------ | ----------- |
| RelativeLayout | 3325842      | 947464       | 108585      |
| FrameLayout    | 3159841      | 879161       | 112988      |
| FlexLayout     | 5278923      | 796837       | 111414      |

### Complex Layout

|                | inflate (ns) | measure (ns) | layout (ns) |
| -------------- | ------------ | ------------ | ----------- |
| RelativeLayout | 17479435     | 2268045      | 822163      |
| GridLayout     | 20350271     | 3270156      | 1177185     |
| FlexLayout     | 21698676     | 2703914      | 1001549     |

You can check the layout xml files [here](https://github.com/Piasy/AndroidPlayground/tree/4a3e49613764d4eec4b48b0eee29b1ea70a027c2/LayoutPerfDemo/src/main/res/layout)

FlexLayout usually takes longer to inflate, but it's equally fast in measure and layout. Normally you use less hierarchy and views than RelativeLayout or LinearLayout, so the overall time spend is competitively, especially when comes to complex layouts.

# Changelog

### 1.2.1 (2016-4-8)

Support parent.visible, parent.gone, parent.tag

### 1.2.0 (2016-4-6)

Show source code position in XML when throw Exceptions. (Syntax exception, Circular dependency, etc.)

### 1.1.0 (2016-3-20)

Initial release to jcenter. Including percentage, view reference, ?= expressions, logic operators.

