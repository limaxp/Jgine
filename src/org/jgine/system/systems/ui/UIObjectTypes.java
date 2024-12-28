package org.jgine.system.systems.ui;

import java.util.function.Supplier;

import org.jgine.system.systems.ui.objects.UIBar;
import org.jgine.system.systems.ui.objects.UIGrid;
import org.jgine.system.systems.ui.objects.UIHotbar;
import org.jgine.system.systems.ui.objects.UILabel;
import org.jgine.system.systems.ui.objects.UIList;
import org.jgine.system.systems.ui.objects.UIRadioButton;
import org.jgine.system.systems.ui.objects.UIScrollBar;
import org.jgine.system.systems.ui.objects.UISlideBar;
import org.jgine.system.systems.ui.objects.UITextInput;
import org.jgine.utils.registry.Registry;

public class UIObjectTypes {

	public static final UIObjectType<UICompound> COMPOUND = a("compound", UICompound::new);

	public static final UIObjectType<UIWindow> WINDOW = a("window", UIWindow::new);

	public static final UIObjectType<UIHotbar> HOTBAR = a("hotbar", UIHotbar::new);

	public static final UIObjectType<UIScrollBar> SCROLLBAR = a("scrollbar", UIScrollBar::new);

	public static final UIObjectType<UISlideBar> SLIDEBAR = a("slidebar", UISlideBar::new);

	public static final UIObjectType<UILabel> LABEL = a("label", UILabel::new);

	public static final UIObjectType<UITextInput> TEXT_INPUT = a("input", UITextInput::new);

	public static final UIObjectType<UIBar> BAR = a("bar", UIBar::new);

	public static final UIObjectType<UIRadioButton> RADIO_BUTTON = a("radio", UIRadioButton::new);

	public static final UIObjectType<UIList> LIST = a("list", UIList::new);

	public static final UIObjectType<UIGrid> GRID = a("grid", UIGrid::new);

	public static <T extends UIObject> UIObjectType<T> a(String name, Supplier<T> supplier) {
		UIObjectType<T> type = new UIObjectType<T>(name, supplier);
		type.setId(Registry.UI_OBJECTS_TYPES.register(name, type));
		return type;
	}

	public static UIObjectType<?> get(String name) {
		return Registry.UI_OBJECTS_TYPES.get(name);
	}

	public static UIObjectType<?> get(int id) {
		return Registry.UI_OBJECTS_TYPES.get(id);
	}
}
