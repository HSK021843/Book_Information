package bookinfo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import bookinfo.common.FileUtils;
import bookinfo.dto.BookCoverDto;
import bookinfo.dto.BookinfoDto;
import bookinfo.mapper.bookinfoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookinfoServiceImpl implements BookinfoService {
	@Autowired
	private bookinfoMapper bookinfoMapper;

	@Autowired
	private FileUtils fileUtils;

	@Override
	public List<BookinfoDto> selectBookList() {
		return bookinfoMapper.selectBookList();
	}

	@Override
	public void insertBookinfo(BookinfoDto bookinfoDto, MultipartHttpServletRequest request) {
		bookinfoMapper.insertBookinfo(bookinfoDto);

		try {
			List<BookCoverDto> fileInfoList = fileUtils.parseFileInfo(bookinfoDto.getBookId(), request);

			if (!CollectionUtils.isEmpty(fileInfoList)) {
				bookinfoMapper.insertBookCoverFileList(fileInfoList);
			}
		} catch (Exception e) {

		}
	}

	@Override
	public BookinfoDto selectBookDetail(int bookId) {
		BookinfoDto bookinfoDto = bookinfoMapper.selectBookDetail(bookId);
		
		if (bookinfoDto != null) {
			List<BookCoverDto> bookCoverFileInfoList = bookinfoMapper.selectBookCoverFileList(bookId);
			bookinfoDto.setFileInfoList(bookCoverFileInfoList);
		}		

		return bookinfoDto;
	}

	@Override
	public void updateBookinfo(BookinfoDto bookinfoDto) {
		bookinfoMapper.updateBookinfo(bookinfoDto);
	}

	@Override
	public void deleteBookinfo(int bookId) {
		BookinfoDto bookinfoDto = new BookinfoDto();
		bookinfoDto.setBookId(bookId);

		bookinfoMapper.deleteBookinfo(bookinfoDto);
	}

	@Override
	public BookCoverDto selectBookCoverFileInfo(int imageId, int bookId) {
		return bookinfoMapper.selectBookCoverFileInfo(imageId, bookId);
	}
}
