package bookinfo.dto;

import lombok.Data;

@Data
public class BookCoverDto {
	private int imageId;
	private int bookId;
	private String imageUrl;
	private String createdAt;
}
