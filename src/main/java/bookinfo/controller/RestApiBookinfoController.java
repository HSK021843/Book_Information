package bookinfo.controller;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import bookinfo.common.FileUtils;
import bookinfo.dto.BookCoverDto;
import bookinfo.dto.BookinfoDto;
import bookinfo.service.BookinfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class RestApiBookinfoController {
	@Autowired
	private BookinfoService bookinfoService;
	
	@Autowired
	private FileUtils fileUtils;

	@GetMapping("/bookinfo")
	public List<BookinfoDto> openBookList() throws Exception {
		return bookinfoService.selectBookList();
	}

	@GetMapping("/bookinfo/write")
	public String openInfoWrite() throws Exception {
		return "/bookinfo/restBookWrite";
	}
	
	@PostMapping(value = "/bookinfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String insertBookInfo(@RequestParam("bookinfo") String bookinfoData, MultipartHttpServletRequest request) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		BookinfoDto bookinfoDto = objectMapper.readValue(bookinfoData, BookinfoDto.class);
		bookinfoService.insertBookinfo(bookinfoDto, request);

		return "redirect:/bookinfo";
	}

	@GetMapping("/bookinfo/{bookId}")
	public ResponseEntity<Object> openBookDetail(@PathVariable("bookId") int bookId) throws Exception {
		BookinfoDto bookinfoDto = bookinfoService.selectBookDetail(bookId);
		
		if (bookinfoDto == null) {
			Map<String, Object> result = new HashMap<>();
			result.put("code", HttpStatus.NOT_FOUND.value());
			result.put("name", HttpStatus.NOT_FOUND.name());
			result.put("message", "책 관리번호 " + bookId + "와 일치하는 데이터가 없습니다.");
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(bookinfoDto);
		}
	}
	
	@PutMapping("/bookinfo/{bookId}")
	public void updateBookinfo(@PathVariable("bookId") int bookId, @RequestBody BookinfoDto bookinfoDto) throws Exception {
		bookinfoDto.setBookId(bookId);
		bookinfoService.updateBookinfo(bookinfoDto);
	}
	
	@PostMapping("/bookinfo/{bookId}")
	public void deleteBookinfo(@RequestParam("bookId") int bookId) throws Exception {
		bookinfoService.deleteBookinfo(bookId);
	}

	@GetMapping("/bookinfo/file")
	public void downloadBookCover(@RequestParam("imageId") int imageId,
								  @RequestParam("bookId") int bookId,
								  HttpServletResponse response) throws Exception {
		BookCoverDto bookCoverDto = bookinfoService.selectBookCoverFileInfo(imageId, bookId);
		BookinfoDto bookinfoDto = bookinfoService.selectBookDetail(bookId);

		if (ObjectUtils.isEmpty(bookCoverDto)) {
			return;
		}
		
		String imageUrl = bookCoverDto.getImageUrl();
		if (imageUrl.startsWith("/") || imageUrl.startsWith("\\")) {
	        imageUrl = imageUrl.substring(1);
	    }
				

		Path path = Paths.get(fileUtils.getUploadDir(), imageUrl);
		byte[] file = Files.readAllBytes(path);
		
		String ext = "";
	    int dotIndex = imageUrl.lastIndexOf('.');
	    if (dotIndex != -1) {
	        ext = imageUrl.substring(dotIndex);
	    }

		response.setContentType("application/octet-stream");
		response.setContentLength(file.length);
		response.setHeader("Content-Disposition",
				"attachment; fileName=\"" + URLEncoder.encode(bookinfoDto.getTitle(), "UTF-8") + ext + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.getOutputStream().write(file);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}
