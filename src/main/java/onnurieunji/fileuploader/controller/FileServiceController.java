package onnurieunji.fileuploader.controller;

import onnurieunji.fileuploader.dto.DeleteRequest;
import onnurieunji.fileuploader.dto.FileDataDTO;
import onnurieunji.fileuploader.dto.PositionsRequest;
import onnurieunji.fileuploader.storage.StorageException;
import onnurieunji.fileuploader.storage.StorageFileNotFoundException;
import onnurieunji.fileuploader.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileServiceController {
    //private final StorageService storageService;

    @Autowired
    private StorageService storageService;
//    public FileServiceController(StorageService storageService) {
//        this.storageService = storageService;
//    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        List<FileDataDTO> fileDatas = storageService.loadAll().map(
                p->new FileDataDTO(p.toString(),
                        MvcUriComponentsBuilder.fromMethodName(FileServiceController.class,
                                "serveFileByURL", p.getFileName().toString()).build().toUri().toString(),
                        MvcUriComponentsBuilder.fromMethodName(FileServiceController.class,
                                "serveFileByDownload", p.getFileName().toString()).build().toUri().toString(),
                        0))
                .collect(Collectors.toList());
        //System.out.println(files.toString());
        model.addAttribute("fileDatas", fileDatas);
        return "uploadForm";
    }
    @GetMapping("/filecontrol")
    public String fileControl(Model model) throws IOException {
        List<FileDataDTO> fileDatas = storageService.loadAll().map(
                        p->new FileDataDTO(p.toString(),
                                MvcUriComponentsBuilder.fromMethodName(FileServiceController.class,
                                        "serveFileByURL", p.getFileName().toString()).build().toUri().toString(),
                                MvcUriComponentsBuilder.fromMethodName(FileServiceController.class,
                                        "serveFileByDownload", p.getFileName().toString()).build().toUri().toString(),
                                0))
                .collect(Collectors.toList());
        //System.out.println(files.toString());
        model.addAttribute("fileDatas", fileDatas);
        return "file-control";
    }
    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFileByDownload(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        String encodedURL = "";

        try{
            encodedURL = URLEncoder.encode(file.getFilename(), "UTF-8");
        }
        catch (UnsupportedEncodingException e){
            throw new AssertionError("UTF-8 is unknown");
        }

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + encodedURL + "\"").body(file);
    }

    @GetMapping(value = "/files/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> serveFileByURL(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().body(file);
    }

    @PostMapping("/postfile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if(file.isEmpty()){
            return "redirect:/";
        }
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PostMapping("/postfiles")
    public String handleMultiFileUpload(@RequestParam("files") List<MultipartFile> files,
                                        RedirectAttributes redirectAttributes) {
        if(files.get(0).isEmpty()){
            return "redirect:/";
        }
        storageService.multiStore(files);
        String fileData = "";
        int fileCount = 0;
        for(MultipartFile file : files){
            fileData += file.getOriginalFilename() + "   ";
            fileCount += 1;
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded [" + fileCount + "] file");
        return "redirect:/";
    }

    @PostMapping("/deleteAll")
    public String deleteAllFiles(@RequestParam("delete-confirm") String confirm,
                                 RedirectAttributes redirectAttributes) {
        if(confirm.equals("delete")){
            storageService.deleteAll();
            redirectAttributes.addFlashAttribute("message",
                    "전체 파일이 삭제되었습니다.");
            return "redirect:/";
        }
        return "redirect:/";
    }

    @PostMapping("/deleteFile")
    public String deleteFile(@RequestBody DeleteRequest deleteRequest,
                                 RedirectAttributes redirectAttributes) {
//        storageService.deleteFile(fileName);
        redirectAttributes.addFlashAttribute("message",
                "파일이 삭제되었습니다.");
        return "redirect:/";
    }

    @PostMapping("/deletefile")
    public String join(@RequestBody PositionsRequest positionsRequest,
                                     RedirectAttributes redirectAttributes) {
        for(String fileName : positionsRequest.getPositions()){
            storageService.deleteFile(fileName);
        }
        redirectAttributes.addFlashAttribute("message",
                "파일이 삭제되었습니다.");
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
