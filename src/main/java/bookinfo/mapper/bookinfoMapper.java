package bookinfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import bookinfo.dto.BookCoverDto;
import bookinfo.dto.BookinfoDto;

@Mapper
public interface bookinfoMapper {
	List<BookinfoDto> selectBookList();
	void insertBookinfo(BookinfoDto bookinfoDto);
	BookinfoDto selectBookDetail(int bookId);
	void updateBookinfo(BookinfoDto bookinfoDto);
	void deleteBookinfo(BookinfoDto bookinfoDto);
	void insertBookCoverFileList(List<BookCoverDto> fileInfoList);
	List<BookCoverDto> selectBookCoverFileList(int bookId);
	BookCoverDto selectBookCoverFileInfo(@Param("imageId") int imageId, @Param("bookId") int bookId);
}
