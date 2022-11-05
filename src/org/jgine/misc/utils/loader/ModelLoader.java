package org.jgine.misc.utils.loader;

import static org.lwjgl.assimp.Assimp.aiGetCompileFlags;
import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiGetExportFormatCount;
import static org.lwjgl.assimp.Assimp.aiGetExportFormatDescription;
import static org.lwjgl.assimp.Assimp.aiGetImportFormatCount;
import static org.lwjgl.assimp.Assimp.aiGetImportFormatDescription;
import static org.lwjgl.assimp.Assimp.aiGetLegalString;
import static org.lwjgl.assimp.Assimp.aiGetVersionMajor;
import static org.lwjgl.assimp.Assimp.aiGetVersionMinor;
import static org.lwjgl.assimp.Assimp.aiGetVersionRevision;
import static org.lwjgl.assimp.Assimp.aiImportFileEx;
import static org.lwjgl.assimp.Assimp.aiImportFileFromMemory;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgine.misc.utils.FileUtils;
import org.jgine.render.graphic.mesh.Model;
import org.lwjgl.assimp.AIExportFormatDesc;
import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIFileIO;
import org.lwjgl.assimp.AIImporterDesc;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

public class ModelLoader {

	private static final HashSet<String> IMPORT_EXTENSIONS;

	static {
		List<ModelFormat> inputFormats = getInputFormats();
		IMPORT_EXTENSIONS = new HashSet<String>();
		for (int i = 0; i < inputFormats.size(); i++)
			IMPORT_EXTENSIONS.add(inputFormats.get(i).getExtension());
	}

	public static boolean supportsImportExtension(String extension) {
		return IMPORT_EXTENSIONS.contains(extension);
	}

	public static Set<String> getImportExtensions() {
		return Collections.unmodifiableSet(IMPORT_EXTENSIONS);
	}

	private static AIFileIO fileIo = AIFileIO.create().OpenProc((pFileIO, fileName, openMode) -> {
		ByteBuffer data;
		String fileNameUtf8 = memUTF8(fileName);
		try {
			data = FileUtils.readByteBuffer(fileNameUtf8);
		} catch (IOException e) {
			throw new RuntimeException("Could not open file: " + fileNameUtf8);
		}

		return AIFile.create().ReadProc((pFile, pBuffer, size, count) -> {
			long max = Math.min(data.remaining(), size * count);
			memCopy(memAddress(data) + data.position(), pBuffer, max);
			return max;
		}).SeekProc((pFile, offset, origin) -> {
			if (origin == Assimp.aiOrigin_CUR) {
				data.position(data.position() + (int) offset);
			} else if (origin == Assimp.aiOrigin_SET) {
				data.position((int) offset);
			} else if (origin == Assimp.aiOrigin_END) {
				data.position(data.limit() + (int) offset);
			}
			return 0;
		}).FileSizeProc(pFile -> data.limit()).address();
	}).CloseProc((pFileIO, pFile) -> {
		AIFile aiFile = AIFile.create(pFile);

		aiFile.ReadProc().free();
		aiFile.SeekProc().free();
		aiFile.FileSizeProc().free();
	});

	public static void free() {
		fileIo.OpenProc().free();
		fileIo.CloseProc().free();
	}

	public static Model load(String path) {
		return load("", path);
	}

	public static Model load(String name, String path) {
		AIScene scene = aiImportFileEx(path, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate, fileIo);
		if (scene == null)
			throw new IllegalStateException(aiGetErrorString());
		return new Model(name, scene);
	}

	/**
	 * Only works with file formats which don't spread their content onto multiple
	 * files, such as .obj or .md3.
	 * 
	 * @param buffer
	 * @return
	 */
	public static Model load(ByteBuffer buffer) {
		return load("", buffer);
	}

	/**
	 * Only works with file formats which don't spread their content onto multiple
	 * files, such as .obj or .md3.
	 * 
	 * @param buffer
	 * @return
	 */
	public static Model load(String name, ByteBuffer buffer) {
		AIScene scene = aiImportFileFromMemory(buffer, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate,
				(ByteBuffer) null);
		if (scene == null)
			throw new IllegalStateException(aiGetErrorString());
		return new Model(name, scene);
	}

	public static String getAiLegalString() {
		return aiGetLegalString();
	}

	public static int getAiVersionMajor() {
		return aiGetVersionMajor();
	}

	public static int getAiVersionMinor() {
		return aiGetVersionMinor();
	}

	public static int getAiVersionRevision() {
		return aiGetVersionRevision();
	}

	public static String getAiVersion() {
		return getAiVersionMajor() + "." + getAiVersionMinor() + "." + getAiVersionRevision();
	}

	public static int getAiCompileFlags() {
		return aiGetCompileFlags();
	}

	public static List<ModelFormat> getInputFormats() {
		long count = aiGetImportFormatCount();
		List<ModelFormat> list = new ArrayList<ModelFormat>((int) count);
		for (int i = 0; i < count; i++) {
			AIImporterDesc desc = aiGetImportFormatDescription(i);
			ModelFormat modelFormat = new ModelFormat();
			modelFormat.name = desc.mNameString();
			modelFormat.extension = desc.mFileExtensionsString();
			modelFormat.id = modelFormat.extension;
			modelFormat.comments = desc.mCommentsString();
			list.add(modelFormat);
		}
		return list;
	}

	public static List<ModelFormat> getOutputFormats() {
		long count = aiGetExportFormatCount();
		List<ModelFormat> list = new ArrayList<ModelFormat>((int) count);
		for (int i = 0; i < count; i++) {
			AIExportFormatDesc desc = aiGetExportFormatDescription(i);
			ModelFormat modelFormat = new ModelFormat();
			modelFormat.name = desc.descriptionString();
			modelFormat.extension = desc.fileExtensionString();
			modelFormat.id = desc.idString();
			modelFormat.comments = "";
			list.add(modelFormat);
		}
		return list;
	}

	public static class ModelFormat {

		private String name;
		private String extension;
		private String id;
		private String comments;

		private ModelFormat() {
		}

		public String getName() {
			return name;
		}

		public String getExtension() {
			return extension;
		}

		public String getId() {
			return id;
		}

		public String getComments() {
			return comments;
		}
	}
}