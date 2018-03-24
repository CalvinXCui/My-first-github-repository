package com.kanq.platform.kqoa.bpmn.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件上传时的工具类
 * 
 * @author ymajm
 *
 */
public class AttachmentRealPathUtils {
	/**
	 * 各个模块需要存放的父目录
	 */
	private static String SIGN_ATTACHMENT_NAME = "EGovernment-File";

	/**
	 * 
	 * @param request
	 * @param distPath
	 * @return
	 */
	public static String getRealPath(HttpServletRequest request, String distPath) {

		String svrCtxPath = request.getSession().getServletContext().getRealPath("/");

		String webappsPath = new File(svrCtxPath).getParent();

		String attachmentPath = Paths.get(webappsPath, SIGN_ATTACHMENT_NAME).toFile().getPath();

		distPath = distPath.replaceAll("/", File.separator).replaceAll("//", File.separator);

		if (!distPath.startsWith(File.separator)) {
			distPath = File.separator + distPath;

		}

		return attachmentPath + distPath;

	}

	/**
	 * 
	 * @param request
	 * @param distFile
	 *            个人自定义的目录
	 * @return
	 */
	public static File getRealFile(HttpServletRequest request, String distFile) {

		String path = getRealPath(request, distFile);

		File file = new File(path);

		File parentFile = file.getParentFile();

		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		return file;
	}

	/**
	 * 获得保存文件的唯一ID
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getAttachmentFileId(HttpServletRequest request, String attachPath) throws Exception {
		String uuid = UUID.randomUUID().toString();

		String saveFilePath = attachPath + File.separator + uuid;
		File targetFile = AttachmentRealPathUtils.getRealFile(request, saveFilePath);

		if (!targetFile.exists()) {
			targetFile.createNewFile();
			return uuid;
		} else {
			return getAttachmentFileId(request, saveFilePath);
		}
	}

	/**
	 * 保存上传的附件
	 * @param request
	 * @param scfile   附件资源数组
	 * @param personalPath  个人定义的路径
	 * @return
	 * @throws Exception
	 */
	public static String saveFile(HttpServletRequest request,MultipartFile[] scfile,String personalPath) throws Exception {
		String AttachmentName="";
		if (!ObjectUtils.isEmpty(scfile)) {
			for (MultipartFile multipartFile : scfile) {
				if (multipartFile.isEmpty()) {
					return null;
				}
				String attachPath = ActivitiAttach.ATTACHMENT_PATH + File.separator + personalPath;;
				// 获取附件名称  
				String uuid = AttachmentRealPathUtils.getAttachmentFileId(request, attachPath);
				File saveFile = AttachmentRealPathUtils.getRealFile(request, attachPath + File.separator + uuid);
				// 向附件表中存入数据
				multipartFile.transferTo(saveFile);
				AttachmentName=multipartFile.getOriginalFilename();
			}
		}
		return AttachmentName;
	}
	// private static String getFileMD5(File file) throws Exception {
	// MessageDigest md = MessageDigest.getInstance("MD5");
	// try (FileInputStream fis = new FileInputStream(file)) {
	// byte[] buffer = new byte[1024];
	// int length = -1;
	// while ((length = fis.read(buffer, 0, 1024)) != -1) {
	// md.update(buffer, 0, length);
	// }
	// }
	//
	// BigInteger bigInt = new BigInteger(1, md.digest());
	// return bigInt.toString(16);
	// }
	//
	// private static String getBytesMD5(byte[] bytes) throws Exception {
	// MessageDigest md = MessageDigest.getInstance("MD5");
	// md.update(bytes, 0, bytes.length);
	//
	// BigInteger bigInt = new BigInteger(1, md.digest());
	// return bigInt.toString(16);
	// }
	//
	// public static boolean checksumFile(byte[] bytes, File srcFile) throws
	// Exception {
	// return getFileMD5(srcFile).equals(getBytesMD5(bytes));
	// }

}
