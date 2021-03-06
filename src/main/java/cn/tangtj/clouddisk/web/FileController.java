package cn.tangtj.clouddisk.web;

import cn.tangtj.clouddisk.entity.UploadFile;
import cn.tangtj.clouddisk.entity.User;
import cn.tangtj.clouddisk.entity.vo.Files;
import cn.tangtj.clouddisk.service.FileService;
import cn.tangtj.clouddisk.service.UserService;
import cn.tangtj.clouddisk.utils.FileUtil;
import cn.tangtj.clouddisk.utils.StringUtil;
import cn.tangtj.clouddisk.utils.UserUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/file")
public class FileController {

    private final String fileSavePath;

    private final static String fileSaveDir = "upload";

    private final UserService userService;

    private final FileService fileService;

    @Autowired
    public FileController(UserService userService, FileService fileService, ServletContext servletContext) {
        this.userService = userService;
        this.fileService = fileService;
        fileSavePath = servletContext.getRealPath("") + fileSaveDir + File.separator;

        //文件的真正保存路径D:、WORK、毕设、CloudDisk、target、spingmvcquickstart、upload
        System.out.println(fileSavePath);

        File file = new File(fileSavePath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @RequestMapping()
    public String index(Model model) {
        User user = UserUtil.getPrincipal();
        if (user == null) {
            return "redirect:/login";
        }
        //11、查询文件列表，返回files.jsp
        List<UploadFile> list = fileService.findByUserId(user.getId());
        Files files = new Files(list);
        model.addAttribute("user", user);
        model.addAttribute("files", files);
        return "files";
    }

    @RequestMapping(value = "/upload")
    @ResponseBody
    public String fileUpload(MultipartFile file) {
        User user = UserUtil.getPrincipal();

        if (user != null && file != null && !file.isEmpty()) {
            //12、点击上传，创建文件信息对象
            UploadFile f = new UploadFile();
            f.setFileName(file.getOriginalFilename());
            f.setUserId(user.getId());
            f.setFileSize(file.getSize());
            f.setUploadDate(new Date());
            String md5Name = StringUtil.str2md5(f.getFileName() + "," + f.getUserId() + "," + f.getUploadDate().toString());

            Files files = new Files(fileService.findByUserId(user.getId()));

            if(files.getFilesSize() + f.getFileSize() > user.getFileMaxSize()){
                return "upload.fail";
            }
            if (files.getFilesCount() + 1 > user.getFileMaxCount()){
                return "upload,fail";
            }

            if (md5Name == null) {
                return "upload,fail";
            }
            f.setMappingName(md5Name);
            File localFile = new File(fileSavePath + f.getMappingName());
            try {
                //使用transferTo()方法将上传文件写到服务器上指定的文件
                file.transferTo(localFile);
                //13、调用service，保存文件信息
                fileService.save(f);
                return "upload,success";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "upload,fail";
    }

    @RequestMapping("/download/{fileId}")
    public ResponseEntity<byte[]> fileDownload(@PathVariable("fileId") int fileId) {

        UploadFile fileInfo = fileService.findById(fileId);
        //14、返回前端，下载文件
        return FileUtil.createResponseEntityByFileInfo(fileInfo, fileSavePath);
    }

    @RequestMapping(value = "/shareFile", produces = "text/json; charset=utf-8")
    @ResponseBody
    public  String shareFile(String fileIdStr) {
        if (fileIdStr == null) {
            return "分享失败";
        }
        int fileId = Integer.parseInt(fileIdStr);
        //15、获得文件名，验证是否存在
        UploadFile fileInfo = fileService.findById(fileId);
        if (fileInfo != null) {
            //16、调用Service层对象，分享文件
            if (fileService.shareFileById(fileInfo.getId()) != null) {
                return "分享成功";
            }
        }
        return "分享失败";
    }

    @RequestMapping(value = "/unShareFile", produces = "text/json; charset=utf-8")
    @ResponseBody
    public String unShareFile(String fileIdStr) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (fileIdStr == null) {
            return "取消失败";
        }
        int fileId = Integer.parseInt(fileIdStr);
        List<UploadFile> list = fileService.findByUserId(user.getId());
        if (list.stream().anyMatch(it->it.getId() == fileId)) {
            fileService.unshareFile(fileId);
            return "取消成功";
        }
        return "取消失败";
    }

    @RequestMapping("/delete/{fileId}")
    public String fileDelete(@PathVariable("fileId") int fileId) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        List<UploadFile> list = fileService.findByUserId(user.getId());
        list.stream().filter(it->it.getId()==fileId).findFirst().ifPresent(it->{
            //17、删除数据库中信息
            fileService.deleteById(fileId);
            File file = new File(fileSavePath + it.getMappingName());
            if (file.exists()){
                //18、删除实际文件
                file.delete();
            }
        });
        return "redirect:/file";
    }


    @RequestMapping("/cancel")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/";
    }
}
