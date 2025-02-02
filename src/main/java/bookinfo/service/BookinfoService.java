package bookinfo.service;

import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import bookinfo.dto.BookCoverDto;
import bookinfo.dto.BookinfoDto;

public interface BookinfoService {
	List<BookinfoDto> selectBookList();
	void insertBookinfo(BookinfoDto bookinfoDto, MultipartHttpServletRequest request);
	BookinfoDto selectBookDetail(int bookId);
	void updateBookinfo(BookinfoDto bookinfoDto);
	void deleteBookinfo(int bookId);
	BookCoverDto selectBookCoverFileInfo(int imageId, int bookId);
}
