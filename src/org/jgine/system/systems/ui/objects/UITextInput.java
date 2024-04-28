package org.jgine.system.systems.ui.objects;

import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.core.input.device.Keyboard;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;

import maxLibs.utils.scheduler.Scheduler;
import maxLibs.utils.scheduler.Task;

public class UITextInput extends UILabel {

	private FocusTask focusTask;

	@Override
	public UITextInput clone() {
		UITextInput obj = (UITextInput) super.clone();
		obj.focusTask = null;
		return obj;
	}

	@Override
	public void onClick(int key) {
		super.onClick(key);
		if (focusTask == null || focusTask.isCanceled())
			Scheduler.runTaskTimer(20, focusTask = new FocusTask(this));
	}

	@Override
	public UIObjectType<? extends UILabel> getType() {
		return UIObjectTypes.TEXT_INPUT;
	}

	public class FocusTask extends Task {

		private UITextInput label;
		private boolean showPos;
		private String text;
		private int pos;
		private int tickTimer;
		private int clickCooldown;

		public FocusTask(UITextInput label) {
			this.label = label;
			text = label.getText().getText();
			pos = text.length();
			Input.setCharCallback((window, codepoint) -> {
				String s = new String(new int[] { codepoint }, 0, 1);
				text = buildText(s.charAt(0));
				label.getText().setText(text);
				pos += 1;
			});
		}

		@Override
		public void cancel() {
			super.cancel();
			Input.setCharCallback(null);
		}

		@Override
		public void run() {
			if (tickTimer++ >= 5) {
				tickTimer = 0;
				if (showPos)
					label.getText().setText(buildText('|'));
				else
					label.getText().setText(buildText(' '));
				showPos = !showPos;
			}

			if (clickCooldown > 0)
				clickCooldown--;
			else
				checkClicks();
		}

		private void checkClicks() {
			Keyboard keyboard = Input.getKeyboard();
			if (keyboard.isPressedIntern(Key.KEY_ESCAPE) || keyboard.isPressedIntern(Key.KEY_ENTER)) {
				label.getText().setText(text);
				cancel();
			} else if (keyboard.isPressedIntern(Key.KEY_LEFT)) {
				if (pos > 0)
					pos--;
				clickCooldown = 5;
			} else if (keyboard.isPressedIntern(Key.KEY_RIGHT)) {
				if (pos < label.getText().getText().length() - 1)
					pos++;
				clickCooldown = 5;
			} else if (keyboard.isPressedIntern(Key.KEY_BACKSPACE)) {
				if (pos > 0) {
					pos--;
					text = deleteChar();
				}
				clickCooldown = 5;
			}
		}

		private String buildText(char divider) {
			StringBuilder result = new StringBuilder(text.length() + 1);
			for (int i = 0; i < pos; i++)
				result.append(text.charAt(i));
			result.append(divider);
			for (int i = pos; i < text.length(); i++)
				result.append(text.charAt(i));
			return result.toString();
		}

		private String deleteChar() {
			StringBuilder result = new StringBuilder(text.length() + 1);
			for (int i = 0; i < pos; i++)
				result.append(text.charAt(i));
			for (int i = pos + 1; i < text.length(); i++)
				result.append(text.charAt(i));
			return result.toString();
		}
	}
}
