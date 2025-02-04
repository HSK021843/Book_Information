package bookinfo.controller;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import bookinfo.common.FileUtils;
import bookinfo.dto.BookCoverDto;
import bookinfo.dto.BookinfoDto;
import bookinfo.service.BookinfoService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class RestController {
	@Autowired
	private BookinfoService bookinfoService;
	
	@Autowired
	private FileUtils fileUtils;

	@GetMapping("/bookinfo")
	public ModelAndView openBookList() throws Exception {
		ModelAndView mv = new ModelAndView("/bookinfo/restBookList");

		List<BookinfoDto> list = bookinfoService.selectBookList();
		mv.addObject("list", list);

		return mv;
	}

	@GetMapping("/bookinfo/write")
	public String openInfoWrite() throws Exception {
		return "/bookinfo/restInfoWrite";
	}
	
	@PostMapping("/bookinfo/write")
	public String insertBookInfo(BookinfoDto bookinfoDto, MultipartHttpServletRequest request) throws Exception {
		bookinfoService.insertBookinfo(bookinfoDto, request);

		return "redirect:/bookinfo/openBookList.do";
	}

	@GetMapping("/bookinfo/{bookId}")
	public ModelAndView openBookDetail(@RequestParam("bookId") int bookId) throws Exception {
		BookinfoDto bookinfoDto = bookinfoService.selectBookDetail(bookId);

		ModelAndView mv = new ModelAndView("/bookinfo/restBookDetail");
		mv.addObject("book", bookinfoDto);
		return mv;
	}
	
	@PutMapping("/bookinfo/{bookId}")
	public String updateBookinfo(@PathVariable("bookId") int bookId, BookinfoDto bookinfoDto) throws Exception {
		bookinfoDto.setBookId(bookId);
		bookinfoService.updateBookinfo(bookinfoDto);
		return "redirect:/bookinfo";
	}
	
	@PostMapping("/bookinfo/{bookId}o")
	public String deleteBookinfo(@RequestParam("bookId") int bookId) throws Exception {
		bookinfoService.deleteBookinfo(bookId);
		return "redirect:/bookinfo";
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
