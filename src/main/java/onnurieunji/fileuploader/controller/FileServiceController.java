package onnurieunji.fileuploader.controller;

import onnurieunji.fileuploader.dto.DeleteRequest;
import onnurieunji.fileuploader.dto.PositionsRequest;
import onnurieunji.fileuploader.storage.StorageException;
import onnurieunji.fileuploader.storage.StorageFileNotFoundException;
import onnurieunji.fileuploader.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileServiceController {
    private final StorageService storageService;

    @Autowired
    public FileServiceController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileServiceController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

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

    @PostMapping("/postfile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PostMapping("/postfiles")
    public String handleMultiFileUpload(@RequestParam("files") List<MultipartFile> files,
                                        RedirectAttributes redirectAttributes) {
        storageService.multiStore(files);
        String fileData = "";

        for(MultipartFile file : files){
            fileData += file.getOriginalFilename() + "   ";
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + fileData);
        return "redirect:/";
    }

    @PostMapping("/deleteAll")
    public String deleteAllFiles(@RequestParam("delete-confirm") String confirm,
                                 RedirectAttributes redirectAttributes) {
        if(confirm.equals("delete")){
            storageService.deleteAll();
            redirectAttributes.addFlashAttribute("message",
                    "전체 파일이 삭제되었습니다.");
            System.out.println(confirm + "delete");
            return "redirect:/";
        }
        return "redirect:/";
    }

    @PostMapping("/deleteFile")
    public String deleteFile(@RequestBody DeleteRequest deleteRequest,
                                 RedirectAttributes redirectAttributes) {
//        storageService.deleteFile(fileName);
        System.out.println(deleteRequest);
        redirectAttributes.addFlashAttribute("message",
                "파일이 삭제되었습니다.");
        return "redirect:/";
    }

    @PostMapping("/positions")
    public ResponseEntity<Void> join(@RequestBody PositionsRequest positionsRequest) {
        System.out.println(positionsRequest);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}