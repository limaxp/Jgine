package org.jgine.system.systems.ui;

import java.util.function.Supplier;

import org.jgine.misc.utils.registry.Registry;
import org.jgine.system.systems.ui.objects.UIBar;
import org.jgine.system.systems.ui.objects.UIGrid;
import org.jgine.system.systems.ui.objects.UIHotbar;
import org.jgine.system.systems.ui.objects.UILabel;
import org.jgine.system.systems.ui.objects.UIList;

public class UIObjectTypes {

	public static final UIObjectType<UIWindow> WINDOW = a("window", UIWindow::new);
	
	public static final UIObjectType<UIWindowExt> WINDOW_EXT = a("window_ext", UIWindowExt::new);

	public static final UIObjectType<UIHotbar> HOTBAR = a("hotbar", UIHotbar::new);

	public static final UIObjectType<UILabel> LABEL = a("label", UILabel::new);

	public static final UIObjectType<UIBar> BAR = a("bar", UIBar::new);

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
