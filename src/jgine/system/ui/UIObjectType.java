package jgine.system.ui;

import java.util.function.Supplier;

import jgine.core.Registry;
import jgine.system.ui.objects.UIBar;
import jgine.system.ui.objects.UIGrid;
import jgine.system.ui.objects.UIHotbar;
import jgine.system.ui.objects.UILabel;
import jgine.system.ui.objects.UIList;
import jgine.system.ui.objects.UIRadioButton;
import jgine.system.ui.objects.UIScrollBar;
import jgine.system.ui.objects.UISlideBar;
import jgine.system.ui.objects.UITextInput;

public class UIObjectType<T extends UIObject> implements Supplier<T> {

	public static final UIObjectType<UICompound> COMPOUND = as("compound", UICompound::new);
	public static final UIObjectType<UIWindow> WINDOW = as("window", UIWindow::new);
	public static final UIObjectType<UIHotbar> HOTBAR = as("hotbar", UIHotbar::new);
	public static final UIObjectType<UIScrollBar> SCROLLBAR = as("scrollbar", UIScrollBar::new);
	public static final UIObjectType<UISlideBar> SLIDEBAR = as("slidebar", UISlideBar::new);
	public static final UIObjectType<UILabel> LABEL = as("label", UILabel::new);
	public static final UIObjectType<UITextInput> TEXT_INPUT = as("input", UITextInput::new);
	public static final UIObjectType<UIBar> BAR = as("bar", UIBar::new);
	public static final UIObjectType<UIRadioButton> RADIO_BUTTON = as("radio", UIRadioButton::new);
	public static final UIObjectType<UIList> LIST = as("list", UIList::new);
	public static final UIObjectType<UIGrid> GRID = as("grid", UIGrid::new);

	public static <T extends UIObject> UIObjectType<T> as(String name, Supplier<T> supplier) {
		UIObjectType<T> type = new UIObjectType<T>(name, supplier);
		type.id = Registry.UI_OBJECT.register(name, type);
		return type;
	}

	public final String name;
	private int id;
	private final Supplier<T> supplier;

	public UIObjectType(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	public int id() {
		return id;
	}

	@Override
	public T get() {
		return supplier.get();
	}
}
