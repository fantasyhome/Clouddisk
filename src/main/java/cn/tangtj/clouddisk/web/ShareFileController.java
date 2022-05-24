package cn.tangtj.clouddisk.web;

import cn.tangtj.clouddisk.entity.UploadFile;
import cn.tangtj.clouddisk.entity.User;
import cn.tangtj.clouddisk.entity.vo.ShareFile;
import cn.tangtj.clouddisk.service.FileService;
import cn.tangtj.clouddisk.service.UserService;
import cn.tangtj.clouddisk.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.File;

@Controller
@RequestMapping("/shareFile")
@SessionAttributes({"guest"})
public class ShareFileController {

    private final String fileSavePath;

    private final static String fileSaveDir = "upload";

    private final FileService fileService;

    private final UserService userService;

    @Autowired
    public ShareFileController(FileService fileService, UserService userService,ServletContext servletContext) {
        this.fileService = fileService;
        this.userService = userService;
        fileSavePath = servletContext.getRealPath("") + fileSaveDir + File.separator;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String shareUi(Model model) {
        return "shareFileIndex";
    }

    @RequestMapping(value = "/{shareCode}", method = RequestMethod.POST)
    @ResponseBody
    public ShareFile shareFile( @PathVariable("shareCode") String shareCode) {
        //19、调用Service层对象，获得文件和拥有者信息，封装成一个ShareFile
        UploadFile fileInfo = fileService.findByShareCode(shareCode);
        User user = userService.findById(fileInfo.getUserId());
        ShareFile shareFileInfo = new ShareFile();
        shareFileInfo.setFileName(fileInfo.getFileName());
        shareFileInfo.setOwnName(user.getUsername());
        shareFileInfo.setShareCode(shareCode);
        return shareFileInfo;
    }

    @RequestMapping(value = "/{shareCode}/download")
    public ResponseEntity<byte[]> shareDownload(@PathVariable("shareCode") String shareCode) {
        UploadFile fileInfo = fileService.findByShareCode(shareCode);
        return FileUtil.createResponseEntityByFileInfo(fileInfo, fileSavePath);
    }
}
