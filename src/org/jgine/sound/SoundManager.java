package org.jgine.sound;

import static org.lwjgl.openal.AL10.AL_DISTANCE_MODEL;
import static org.lwjgl.openal.AL10.AL_EXTENSIONS;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.AL_RENDERER;
import static org.lwjgl.openal.AL10.AL_VENDOR;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetInteger;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.openal.AL11.AL_LINEAR_DISTANCE_CLAMPED;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.ALC_EXTENSIONS;
import static org.lwjgl.openal.ALC10.ALC_MAJOR_VERSION;
import static org.lwjgl.openal.ALC10.ALC_MINOR_VERSION;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.ALC11.ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC11.ALC_CAPTURE_DEVICE_SPECIFIER;
import static org.lwjgl.openal.EXTEfx.ALC_EFX_MAJOR_VERSION;
import static org.lwjgl.openal.EXTEfx.ALC_EFX_MINOR_VERSION;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_AUTOWAH;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_CHORUS;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_COMPRESSOR;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_DISTORTION;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_EAXREVERB;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_ECHO;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_EQUALIZER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_FLANGER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_FREQUENCY_SHIFTER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_PITCH_SHIFTER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_REVERB;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_RING_MODULATOR;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_VOCAL_MORPHER;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_BANDPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_HIGHPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_LOWPASS;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.jgine.core.Transform;
import org.jgine.core.manager.SystemManager;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.sound.effect.EFXUtil;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.physic.PhysicSystem;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EnumerateAllExt;
import org.lwjgl.system.MemoryUtil;

public class SoundManager {

	private static SoundDevice device;
	static final List<SoundSource> SOURCES = new UnorderedIdentityArrayList<SoundSource>();
	private static final SoundListener listener = new SoundListener();

	public static void init() {
		setDefaultDevice();
	}

	public static void terminate() {
		for (SoundSource source : SOURCES)
			source.close();
		device.close();
	}

	public static void update() {
		updateListener();
		updateSources();
	}

	private static void updateListener() {
		// TODO this should not access systems!
		CameraSystem cameraSystem = SystemManager.get(CameraSystem.class);
		Camera camera = cameraSystem.getCamera();
		Transform transform = camera.getTransform();
		listener.setPosition(transform.getPosition());
		PhysicObject physicObject = transform.getEntity().getSystem(PhysicSystem.class);
		if (physicObject != null)
			listener.setVelocity(physicObject.getVelocity());
		listener.setOrientation(Vector3f.mult(camera.getForwardDirection(), -1), camera.getUpDirection());
	}

	private static void updateSources() {
		for (int i = SOURCES.size() - 1; i >= 0; i--) {
			SoundSource source = SOURCES.get(i);
			if (source.isStopped()) {
				SOURCES.remove(i);
				source.close();
				continue;
			}
			if (source.isStream())
				source.updateStream();
		}
	}

	public static SoundDevice setDefaultDevice() {
		return setDevice((ByteBuffer) null);
	}

	public static SoundDevice setDevice(ByteBuffer buffer) {
		if (device != null)
			terminate();
		device = new SoundDevice(buffer);
		listener.init(new Vector3f(0, 0, 0));
		setDistanceModel(AL_LINEAR_DISTANCE_CLAMPED);
		return device;
	}

	public static SoundDevice setDevice(CharSequence name) {
		if (device != null)
			terminate();
		device = new SoundDevice(name);
		listener.init(new Vector3f(0, 0, 0));
		setDistanceModel(AL_LINEAR_DISTANCE_CLAMPED);
		return device;
	}

	public static SoundDevice getDevice() {
		return device;
	}

	public static String getDefaultDevice() {
		if (device.deviceCapabilities.ALC_ENUMERATE_ALL_EXT)
			return alcGetString(0, EnumerateAllExt.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
		else
			return alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
	}

	public static List<String> getDevices() {
		return ALUtil.getStringList(MemoryUtil.NULL, ALC_ALL_DEVICES_SPECIFIER);
	}

	public static String getDefaultCaptureDevice() {
		return alcGetString(0, ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER);
	}

	public static List<String> getCaptureDevices() {
		return ALUtil.getStringList(MemoryUtil.NULL, ALC_CAPTURE_DEVICE_SPECIFIER);
	}

	public static SoundListener listener() {
		return listener;
	}

	public static int getALCVersionMajor() {
		return alcGetInteger(device.device, ALC_MAJOR_VERSION);
	}

	public static int getALCVersionMinor() {
		return alcGetInteger(device.device, ALC_MINOR_VERSION);
	}

	public static String getALCVersion() {
		return getALCVersionMajor() + "." + getALCVersionMinor();
	}

	public static String getALCExtensions() {
		return alcGetString(device.device, ALC_EXTENSIONS);
	}

	public static String getALVersion() {
		return alGetString(AL_VERSION);
	}

	public static String getALExtensions() {
		return alGetString(AL_EXTENSIONS);
	}

	public static String getALVendor() {
		return alGetString(AL_VENDOR);
	}

	public static String getALRenderer() {
		return alGetString(AL_RENDERER);
	}

	public static int getEFXVersionMajor() {
		return alcGetInteger(device.device, ALC_EFX_MAJOR_VERSION);
	}

	public static int getEFXVersionMinor() {
		return alcGetInteger(device.device, ALC_EFX_MINOR_VERSION);
	}

	public static String getEFXVersion() {
		return getEFXVersionMajor() + "." + getEFXVersionMinor();
	}

	public static int getMaxAuxiliarySends() {
		return alcGetInteger(device.device, ALC_MAX_AUXILIARY_SENDS);
	}

	public static Stream<Entry<String, Integer>> getSupportedFilter() {
		HashMap<String, Integer> filters = new HashMap<>();
		filters.put("Low-pass", AL_FILTER_LOWPASS);
		filters.put("High-pass", AL_FILTER_HIGHPASS);
		filters.put("Band-pass", AL_FILTER_BANDPASS);

		return filters.entrySet().stream().filter(entry -> EFXUtil.isFilterSupported(entry.getValue()));
	}

	public static Stream<Entry<String, Integer>> getSupportedEffects() {
		HashMap<String, Integer> effects = new HashMap<>();
		effects.put("EAX Reverb", AL_EFFECT_EAXREVERB);
		effects.put("Reverb", AL_EFFECT_REVERB);
		effects.put("Chorus", AL_EFFECT_CHORUS);
		effects.put("Distortion", AL_EFFECT_DISTORTION);
		effects.put("Echo", AL_EFFECT_ECHO);
		effects.put("Flanger", AL_EFFECT_FLANGER);
		effects.put("Frequency Shifter", AL_EFFECT_FREQUENCY_SHIFTER);
		effects.put("Vocal Morpher", AL_EFFECT_VOCAL_MORPHER);
		effects.put("Pitch Shifter", AL_EFFECT_PITCH_SHIFTER);
		effects.put("Ring Modulator", AL_EFFECT_RING_MODULATOR);
		effects.put("Autowah", AL_EFFECT_AUTOWAH);
		effects.put("Compressor", AL_EFFECT_COMPRESSOR);
		effects.put("Equalizer", AL_EFFECT_EQUALIZER);

		return effects.entrySet().stream().filter(e -> EFXUtil.isEffectSupported(e.getValue()));
	}

	public static void setDistanceModel(int parameter) {
		alDistanceModel(parameter);
	}

	public static int getDistanceModel() {
		return alGetInteger(AL_DISTANCE_MODEL);
	}

	private static SoundSource createSource(SoundBuffer buffer, boolean loop, boolean relative) {
		SoundSource source = new SoundSource(buffer, loop, relative);
		SOURCES.add(source);
		return source;
	}

	public static SoundSource play(SoundBuffer buffer, Vector2f pos, float gain) {
		return play(buffer, pos, gain, false, false);
	}

	public static SoundSource play(SoundBuffer buffer, Vector2f pos, float gain, boolean loop) {
		return play(buffer, pos, gain, loop, false);
	}

	public static SoundSource play(SoundBuffer buffer, Vector2f pos, float gain, boolean loop, boolean sourceRelative) {
		return play(buffer, pos.x, pos.y, 0, gain, loop, sourceRelative);
	}

	public static SoundSource play(SoundBuffer buffer, Vector3f pos, float gain) {
		return play(buffer, pos, gain, false, false);
	}

	public static SoundSource play(SoundBuffer buffer, Vector3f pos, float gain, boolean loop) {
		return play(buffer, pos, gain, loop, false);
	}

	public static SoundSource play(SoundBuffer buffer, Vector3f pos, float gain, boolean loop, boolean sourceRelative) {
		return play(buffer, pos.x, pos.y, pos.z, gain, loop, sourceRelative);
	}

	public static SoundSource play(SoundBuffer buffer, float x, float y, float z, float gain) {
		return play(buffer, x, y, z, gain, false, false);
	}

	public static SoundSource play(SoundBuffer buffer, float x, float y, float z, float gain, boolean loop) {
		return play(buffer, x, y, z, gain, loop, false);
	}

	public static SoundSource play(SoundBuffer buffer, float x, float y, float z, float gain, boolean loop,
			boolean sourceRelative) {
		SoundSource source = createSource(buffer, loop, sourceRelative);
		source.setPosition(x, y, z);
		source.setGain(gain);
		source.play();
		return source;
	}

	public static SoundSource play(SoundBuffer buffer, Vector2f pos, Vector2f vel, float gain) {
		return play(buffer, pos, vel, gain, false, false);
	}

	public static SoundSource play(SoundBuffer buffer, Vector2f pos, Vector2f vel, float gain, boolean loop) {
		return play(buffer, pos, vel, gain, loop, false);
	}

	public static SoundSource play(SoundBuffer buffer, Vector2f pos, Vector2f vel, float gain, boolean loop,
			boolean sourceRelative) {
		return play(buffer, pos.x, pos.y, 0, vel.x, vel.y, 0, gain, loop, sourceRelative);
	}

	public static SoundSource play(SoundBuffer buffer, Vector3f pos, Vector3f vel, float gain) {
		return play(buffer, pos, vel, gain, false, false);
	}

	public static SoundSource play(SoundBuffer buffer, Vector3f pos, Vector3f vel, float gain, boolean loop) {
		return play(buffer, pos, vel, gain, loop, false);
	}

	public static SoundSource play(SoundBuffer buffer, Vector3f pos, Vector3f vel, float gain, boolean loop,
			boolean sourceRelative) {
		return play(buffer, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z, gain, loop, sourceRelative);
	}

	public static SoundSource play(SoundBuffer buffer, float x, float y, float z, float velX, float velY, float velZ,
			float gain) {
		return play(buffer, x, y, z, velX, velY, velZ, gain, false, false);
	}

	public static SoundSource play(SoundBuffer buffer, float x, float y, float z, float velX, float velY, float velZ,
			float gain, boolean loop) {
		return play(buffer, x, y, z, velX, velY, velZ, gain, loop, false);
	}

	public static SoundSource play(SoundBuffer buffer, float x, float y, float z, float velX, float velY, float velZ,
			float gain, boolean loop, boolean sourceRelative) {
		SoundSource source = createSource(buffer, loop, sourceRelative);
		source.setPosition(x, y, z);
		source.setVelocity(velX, velY, velZ);
		source.setGain(gain);
		source.play();
		return source;
	}

	public static int getError() {
		return alGetError();
	}

	public static String getErrorString() {
		return alGetString(getError());
	}

	public static void checkError() {
		if (getError() != AL_NO_ERROR)
			throw new RuntimeException(getErrorString());
	}
}
