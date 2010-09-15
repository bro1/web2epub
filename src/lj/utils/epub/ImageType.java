package lj.utils.epub;

public enum ImageType {
  jpg("image/jpeg"),
  gif("image/gif"),
  png("image/png");
  
  private String mimeType;

  private ImageType(String mimeType) {
    this.mimeType = mimeType;
  }
  
  public String getMimeType() {
    return mimeType; 
  }
  
  public static ImageType getType(String type) {
    if ("png".equals(type)) {
      return png;
    } else if ("JPEG".equals(type)) {
      return jpg;
    } else if ("gif".equals(type)) {
      return gif;
    }
    
    return null;
  }
}
