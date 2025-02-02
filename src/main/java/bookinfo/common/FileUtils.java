package bookinfo.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import bookinfo.dto.BookCoverDto;

@Component
public class FileUtils {
	@Value("${spring.servlet.multipart.location}")
	private String uploadDir;
	
	public String getUploadDir() {
		return uploadDir;
	}

	public List<BookCoverDto> parseFileInfo(int bookId, MultipartHttpServletRequest request) throws Exception {
		if (ObjectUtils.isEmpty(request)) {
			return null;
		}

		List<BookCoverDto> fileInfoList = new ArrayList<>();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		ZonedDateTime now = ZonedDateTime.now();
		String folderName = now.format(dtf);
		String storedDir = uploadDir + "images\\" + now.format(dtf);
		File fileDir = new File(storedDir);

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		Iterator<String> fileTagNames = request.getFileNames();
		while (fileTagNames.hasNext()) {
			String fileTagName = fileTagNames.next();
			List<MultipartFile> files = request.getFiles(fileTagName);
			for (MultipartFile file : files) {
				String originalFileExtension = "";

				if (!file.isEmpty()) {
					String contentType = file.getContentType();
					if (ObjectUtils.isEmpty(contentType)) {
						break;
					} else {
						if (contentType.contains("image/jpeg")) {
							originalFileExtension = ".jpg";
						} else if (contentType.contains("image/png")) {
							originalFileExtension = ".png";
						} else if (contentType.contains("image/gif")) {
							originalFileExtension = ".gif";
						} else {
							break;
						}
					}

					String storedFileName = Long.toString(System.nanoTime()) + originalFileExtension;
					String storedFilePath = storedDir + "\\" + storedFileName;
					String imageUrl = "/images/" + folderName + "/" + storedFileName;

					BookCoverDto dto = new BookCoverDto();
					dto.setBookId(bookId);
					dto.setImageUrl(imageUrl);
					DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					dto.setCreatedAt(now.format(dateTimeFormatter));
					fileInfoList.add(dto);

					fileDir = new File(storedFilePath);
					file.transferTo(fileDir);
				}
			}
		}

		return fileInfoList;
	}
}
