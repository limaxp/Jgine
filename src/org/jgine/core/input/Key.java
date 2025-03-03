package org.jgine.core.input;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_A;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_B;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_BACK;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_CIRCLE;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_CROSS;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_GUIDE;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_SQUARE;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_START;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_TRIANGLE;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_4;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_5;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_6;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_7;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_8;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwGetKeyName;
import static org.lwjgl.glfw.GLFW.glfwGetKeyScancode;

/**
 * Base Key class. A key is defined as an id. Use this class to define keys for
 * multiple {@link InputDevice}<code>s</code>.
 */
public class Key {

	public static final int RELEASE = GLFW_RELEASE;
	public static final int PRESS = GLFW_PRESS;
	public static final int REPEAT = GLFW_REPEAT;

	public final static int MOUSE_BUTTON_LEFT = GLFW_MOUSE_BUTTON_LEFT;
	public final static int MOUSE_BUTTON_RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
	public final static int MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;
	public final static int MOUSE_BUTTON_1 = GLFW_MOUSE_BUTTON_1;
	public final static int MOUSE_BUTTON_2 = GLFW_MOUSE_BUTTON_2;
	public final static int MOUSE_BUTTON_3 = GLFW_MOUSE_BUTTON_3;
	public final static int MOUSE_BUTTON_4 = GLFW_MOUSE_BUTTON_4;
	public final static int MOUSE_BUTTON_5 = GLFW_MOUSE_BUTTON_5;
	public final static int MOUSE_BUTTON_6 = GLFW_MOUSE_BUTTON_6;
	public final static int MOUSE_BUTTON_7 = GLFW_MOUSE_BUTTON_7;
	public final static int MOUSE_BUTTON_8 = GLFW_MOUSE_BUTTON_8;
	public final static int MOUSE_BUTTON_LAST = GLFW_MOUSE_BUTTON_LAST;

	public final static int GAMEPAD_AXIS_LEFT_X = GLFW_GAMEPAD_AXIS_LEFT_X;
	public final static int GAMEPAD_AXIS_LEFT_Y = GLFW_GAMEPAD_AXIS_LEFT_Y;
	public final static int GAMEPAD_AXIS_RIGHT_X = GLFW_GAMEPAD_AXIS_RIGHT_X;
	public final static int GAMEPAD_AXIS_RIGHT_Y = GLFW_GAMEPAD_AXIS_RIGHT_Y;
	public final static int GAMEPAD_AXIS_LEFT_TRIGGER = GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
	public final static int GAMEPAD_AXIS_RIGHT_TRIGGER = GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;
	public final static int GAMEPAD_AXIS_LAST = GLFW_GAMEPAD_AXIS_LAST;

	public final static int GAMEPAD_BUTTON_A = GLFW_GAMEPAD_BUTTON_A;
	public final static int GAMEPAD_BUTTON_CROSS = GLFW_GAMEPAD_BUTTON_CROSS;
	public final static int GAMEPAD_BUTTON_B = GLFW_GAMEPAD_BUTTON_B;
	public final static int GAMEPAD_BUTTON_CIRCLE = GLFW_GAMEPAD_BUTTON_CIRCLE;
	public final static int GAMEPAD_BUTTON_X = GLFW_GAMEPAD_BUTTON_X;
	public final static int GAMEPAD_BUTTON_SQUARE = GLFW_GAMEPAD_BUTTON_SQUARE;
	public final static int GAMEPAD_BUTTON_Y = GLFW_GAMEPAD_BUTTON_Y;
	public final static int GAMEPAD_BUTTON_TRIANGLE = GLFW_GAMEPAD_BUTTON_TRIANGLE;
	public final static int GAMEPAD_BUTTON_LEFT_BUMPER = GLFW_GAMEPAD_BUTTON_LEFT_BUMPER;
	public final static int GAMEPAD_BUTTON_RIGHT_BUMPER = GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER;
	public final static int GAMEPAD_BUTTON_BACK = GLFW_GAMEPAD_BUTTON_BACK;
	public final static int GAMEPAD_BUTTON_START = GLFW_GAMEPAD_BUTTON_START;
	public final static int GAMEPAD_BUTTON_GUIDE = GLFW_GAMEPAD_BUTTON_GUIDE;
	public final static int GAMEPAD_BUTTON_LEFT_THUMB = GLFW_GAMEPAD_BUTTON_LEFT_THUMB;
	public final static int GAMEPAD_BUTTON_RIGHT_THUMB = GLFW_GAMEPAD_BUTTON_RIGHT_THUMB;
	public final static int GAMEPAD_BUTTON_DPAD_UP = GLFW_GAMEPAD_BUTTON_DPAD_UP;
	public final static int GAMEPAD_BUTTON_DPAD_RIGHT = GLFW_GAMEPAD_BUTTON_DPAD_RIGHT;
	public final static int GAMEPAD_BUTTON_DPAD_DOWN = GLFW_GAMEPAD_BUTTON_DPAD_DOWN;
	public final static int GAMEPAD_BUTTON_DPAD_LEFT = GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
	public final static int GAMEPAD_BUTTON_LAST = GLFW_GAMEPAD_BUTTON_LAST;

	public final static int KEY_UNKNOWN = GLFW_KEY_UNKNOWN;
	public final static int KEY_LAST = GLFW_KEY_LAST;

	public final static int KEY_0 = 48;
	public final static int KEY_1 = 49;
	public final static int KEY_2 = 50;
	public final static int KEY_3 = 51;
	public final static int KEY_4 = 52;
	public final static int KEY_5 = 53;
	public final static int KEY_6 = 54;
	public final static int KEY_7 = 55;
	public final static int KEY_8 = 56;
	public final static int KEY_9 = 57;

	public final static int KEY_A = 65;
	public final static int KEY_B = 66;
	public final static int KEY_C = 67;
	public final static int KEY_D = 68;
	public final static int KEY_E = 69;
	public final static int KEY_F = 70;
	public final static int KEY_G = 71;
	public final static int KEY_H = 72;
	public final static int KEY_I = 73;
	public final static int KEY_J = 74;
	public final static int KEY_K = 75;
	public final static int KEY_L = 76;
	public final static int KEY_M = 77;
	public final static int KEY_N = 78;
	public final static int KEY_O = 79;
	public final static int KEY_P = 80;
	public final static int KEY_Q = 81;
	public final static int KEY_R = 82;
	public final static int KEY_S = 83;
	public final static int KEY_T = 84;
	public final static int KEY_U = 85;
	public final static int KEY_V = 86;
	public final static int KEY_W = 87;
	public final static int KEY_X = 88;
	public final static int KEY_Y = 89;
	public final static int KEY_Z = 90;

	public final static int KEY_UP = 265;
	public final static int KEY_DOWN = 264;
	public final static int KEY_LEFT = 263;
	public final static int KEY_RIGHT = 262;

	public final static int KEY_ESCAPE = 256;
	public final static int KEY_MINUS = 45;
	public final static int KEY_EQUALS = 61;
	public final static int KEY_BACKSPACE = 259;
	public final static int KEY_SPACE = 32;
	public final static int KEY_TAB = 258;
	public final static int KEY_ENTER = 257;
	public final static int KEY_LCONTROL = 341;
	public final static int KEY_RCONTROL = 345;
	public final static int KEY_LSHIFT = 340;
	public final static int KEY_RSHIFT = 344;
	public final static int KEY_COMMA = 44;
	public final static int KEY_DOT = 46;
	public final static int KEY_LALT = 342;
	public final static int KEY_RALT = 346;
	public final static int KEY_SCROLL_LOCK = 280;
	public final static int KEY_DELETE = 261;

	public final static int KEY_F1 = 290;
	public final static int KEY_F2 = 291;
	public final static int KEY_F3 = 292;
	public final static int KEY_F4 = 293;
	public final static int KEY_F5 = 294;
	public final static int KEY_F6 = 295;
	public final static int KEY_F7 = 296;
	public final static int KEY_F8 = 297;
	public final static int KEY_F9 = 298;
	public final static int KEY_F10 = 299;
	public final static int KEY_F11 = 300;
	public final static int KEY_F12 = 301;
	public final static int KEY_F13 = 302;
	public final static int KEY_F14 = 303;
	public final static int KEY_F15 = 304;
	public final static int KEY_F16 = 305;
	public final static int KEY_F17 = 306;
	public final static int KEY_F18 = 307;
	public final static int KEY_F19 = 308;
	public final static int KEY_F20 = 309;
	public final static int KEY_F21 = 310;
	public final static int KEY_F22 = 311;
	public final static int KEY_F23 = 312;
	public final static int KEY_F24 = 313;
	public final static int KEY_F25 = 314;

	public final static int KEY_NUMLOCK = 282;
	public final static int KEY_NUMPAD0 = 320;
	public final static int KEY_NUMPAD1 = 321;
	public final static int KEY_NUMPAD2 = 322;
	public final static int KEY_NUMPAD3 = 323;
	public final static int KEY_NUMPAD4 = 324;
	public final static int KEY_NUMPAD5 = 325;
	public final static int KEY_NUMPAD6 = 326;
	public final static int KEY_NUMPAD7 = 327;
	public final static int KEY_NUMPAD8 = 328;
	public final static int KEY_NUMPAD9 = 329;
	public final static int KEY_NUMPAD_PLUS = 334;
	public final static int KEY_NUMPAD_COMMA = 330;
	public final static int KEY_NUMPAD_DIVIDE = 331;
	public final static int KEY_NUMPAD_MULTIPLY = 332;
	public final static int KEY_NUMPAD_MINUS = 333;

	public static int getScancode(int key) {
		return glfwGetKeyScancode(key);
	}

	public static String getNamePerKey(int key) {
		return glfwGetKeyName(key, 0);
	}

	public static String getNamePerScancode(int scancode) {
		return glfwGetKeyName(KEY_UNKNOWN, scancode);
	}

	public static String getName(int key, int scancode) {
		return glfwGetKeyName(key, scancode);
	}

	protected int keyboardKey;
	protected int keyboardAltKey;
	protected int mouseKey;
	protected int gamepadKey;

	protected Key() {
	}

	public Key(int keyboardKey, int keyboardAltKey, int mouseKey, int gamepadKey) {
		this.keyboardKey = keyboardKey;
		this.keyboardAltKey = keyboardAltKey;
		this.mouseKey = mouseKey;
		this.gamepadKey = gamepadKey;
	}

	public void setKeyboardKey(int key) {
		this.keyboardKey = key;
	}

	public int getKeyboardKey() {
		return keyboardKey;
	}

	public void setKeyboardAltKey(int key) {
		this.keyboardAltKey = key;
	}

	public int getKeyboardAltKey() {
		return keyboardAltKey;
	}

	public void setMouseKey(int mouseKey) {
		this.mouseKey = mouseKey;
	}

	public int getMouseKey() {
		return mouseKey;
	}

	public void setGamepadKey(int key) {
		this.gamepadKey = key;
	}

	public int getGamepadKey() {
		return gamepadKey;
	}
}