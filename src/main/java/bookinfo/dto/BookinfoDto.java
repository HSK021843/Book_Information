package bookinfo.dto;

import java.util.List;

import lombok.Data;

@Data
public class BookinfoDto {
	private int bookId;
	private String title;
	private String author;
	private String publisher;
	private String date;
	private String Isbn;
	private String text;
	private String createdAt;
	private String updatedAt;

	private List<BookCoverDto> fileInfoList;
}
