package onnurieunji.fileuploader.dto;

public class FileDataDTO {
    public String fileName;
    public String fileFullPath;
    public String fileFullPathDownload;
    public int fileSize;

    public FileDataDTO(){
    }
    public FileDataDTO(String filename,String fileFullPath, String fileFullPathDownload, int fileSize){
        this.fileName = filename;
        this.fileFullPath = fileFullPath;
        this.fileSize = fileSize;
        this.fileFullPathDownload = fileFullPathDownload;
    }
    @Override
    public String toString() {
        return "FileDataDTO{fileName=" + fileName + ", fileFullPath=" + fileFullPath + ", fileFullPathDownload =" + fileFullPathDownload + ", fileSize =" + fileSize + "}";
    }
}
