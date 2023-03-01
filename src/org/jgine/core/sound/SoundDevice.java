package org.jgine.core.sound;

import static org.lwjgl.openal.ALC10.ALC_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.ALC_FREQUENCY;
import static org.lwjgl.openal.ALC10.ALC_NO_ERROR;
import static org.lwjgl.openal.ALC10.ALC_REFRESH;
import static org.lwjgl.openal.ALC10.ALC_SYNC;
import static org.lwjgl.openal.ALC10.ALC_TRUE;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetEnumValue;
import static org.lwjgl.openal.ALC10.alcGetError;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.ALC11.ALC_MONO_SOURCES;
import static org.lwjgl.openal.ALC11.ALC_STEREO_SOURCES;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.openal.SOFTHRTF.ALC_HRTF_ID_SOFT;
import static org.lwjgl.openal.SOFTHRTF.ALC_HRTF_SOFT;
import static org.lwjgl.openal.SOFTHRTF.ALC_HRTF_SPECIFIER_SOFT;
import static org.lwjgl.openal.SOFTHRTF.ALC_NUM_HRTF_SPECIFIERS_SOFT;
import static org.lwjgl.openal.SOFTHRTF.alcGetStringiSOFT;
import static org.lwjgl.openal.SOFTHRTF.alcResetDeviceSOFT;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.misc.utils.logger.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryUtil;

public class SoundDevice {

	public final long device;
	public final long context;
	public final ALCCapabilities deviceCapabilities;
	public final ALCapabilities capabilities;
	public final boolean useTLC;

	SoundDevice(ByteBuffer buffer) {
		this(alcOpenDevice(buffer));
	}

	SoundDevice(CharSequence name) {
		this(alcOpenDevice(name));
	}

	SoundDevice(long device) {
		this.device = device;
		if (device == 0) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		deviceCapabilities = ALC.createCapabilities(device);
		context = alcCreateContext(device, (IntBuffer) null);
		checkError();

		useTLC = deviceCapabilities.ALC_EXT_thread_local_context && alcSetThreadContext(context);
//		if (!useTLC) {
		if (!alcMakeContextCurrent(context)) {
			throw new IllegalStateException();
		}
//		}
		checkError();
		capabilities = AL.createCapabilities(deviceCapabilities);
		initHRTF();
	}

	void close() {
		alcMakeContextCurrent(MemoryUtil.NULL);
		if (useTLC) {
			AL.setCurrentThread(null);
		} else {
			AL.setCurrentProcess(null);
		}
		alcDestroyContext(context);
		alcCloseDevice(device);
	}

	private void initHRTF() {
		IntBuffer attr = BufferUtils.createIntBuffer(10).put(ALC_HRTF_SOFT).put(ALC_TRUE);
		attr.put(ALC_HRTF_ID_SOFT).put(getFunctionalHRTF());
		attr.put(0);
		attr.flip();

		if (!resetDeviceSOFT(attr))
			Logger.warn("SoundDevice '" + getName() + "':Failed to reset device for HRTF! "
					+ alcGetString(device, alcGetError(device)));
		if (!hasHRTF())
			Logger.warn("SoundDevice '" + getName() + "': HRTF not enabled!");
	}

	public int getFunctionalHRTF() {
		int frequency = getFrequency();
		List<String> hrtfs = getHRTFs();
		for (int i = 0; i < hrtfs.size(); i++) {
			String hrtf = hrtfs.get(i);
			int hrtfFrequency;
			try {
				hrtfFrequency = Integer.parseInt(hrtf.substring(hrtf.lastIndexOf(' ') + 1, hrtf.length() - 2));
			} catch (Exception e) {
				continue;
			}
			if (frequency == hrtfFrequency)
				return i;
		}
		return 0;
	}

	public String getName() {
		return alcGetString(device, ALC_DEVICE_SPECIFIER);
	}

	public int getFrequency() {
		return getInt(ALC_FREQUENCY);
	}

	public int getRefresh() {
		return getInt(ALC_REFRESH);
	}

	public boolean isSync() {
		return getInt(ALC_SYNC) == ALC_TRUE;
	}

	public int getMonoSources() {
		return getInt(ALC_MONO_SOURCES);
	}

	public int getStereoSources() {
		return getInt(ALC_STEREO_SOURCES);
	}

	public List<String> getHRTFs() {
		List<String> list = new FastArrayList<String>();
		int num_hrtf = getInt(ALC_NUM_HRTF_SPECIFIERS_SOFT);
		for (int i = 0; i < num_hrtf; i++)
			list.add(getStringSoft(ALC_HRTF_SPECIFIER_SOFT, i));
		return list;
	}

	public boolean hasHRTF() {
		return getInt(ALC_HRTF_SOFT) != 0;
	}

	public String getHRTF() {
		return getString(ALC_HRTF_SPECIFIER_SOFT);
	}

	public boolean resetDeviceSOFT(int[] attribute) {
		return alcResetDeviceSOFT(device, attribute);
	}

	public boolean resetDeviceSOFT(IntBuffer attribute) {
		return alcResetDeviceSOFT(device, attribute);
	}

	public int getInt(int parameter) {
		return alcGetInteger(device, parameter);
	}

	public String getString(int parameter) {
		return alcGetString(device, parameter);
	}

	public String getStringSoft(int parameter, int index) {
		return alcGetStringiSOFT(device, parameter, index);
	}

	public int getEnumValue(ByteBuffer buffer) {
		return alcGetEnumValue(device, buffer);
	}

	public int getEnumValue(CharSequence name) {
		return alcGetEnumValue(device, name);
	}

	public int getError() {
		return alcGetError(device);
	}

	public String getErrorString() {
		return alcGetString(device, getError());
	}

	public void checkError() {
		if (getError() != ALC_NO_ERROR)
			throw new RuntimeException(getErrorString());
	}
}
