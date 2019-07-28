package studentmvc.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import studentmvc.model.FileUpload;
import studentmvc.service.UserService;
import studentmvc.validator.FileValidator;

@Controller
public class FileUploadController {

	@Autowired
	FileValidator fileValidator;

	@Autowired
	UserService userService;

	@RequestMapping(value = "/uploadPage", method = RequestMethod.GET)
	public ModelAndView uploadPage() {
		ModelAndView model = new ModelAndView("upload_page");
		FileUpload formUpload = new FileUpload();
		model.addObject("formUpload", formUpload);
		return model;
	}

	@RequestMapping(value = "/doUpload", method = RequestMethod.POST)
	public String doUpload(@ModelAttribute("formUpload") FileUpload fileUpload, BindingResult result,
			RedirectAttributes redirectAttributes) throws IOException, JAXBException {
		// Validate
		fileValidator.validate(fileUpload, result);

		if (result.hasErrors()) {
			return "upload_page";
		} else {
			// doUpload
			redirectAttributes.addFlashAttribute("fileNames", uploadAndImportDb(fileUpload));
			return "success";
		}
	}

	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public ModelAndView success() {
		ModelAndView model = new ModelAndView("success");
		return model;
	}

	private List<String> uploadAndImportDb(FileUpload fileUpload) throws IOException, JAXBException {
		List<String> fileNames = new ArrayList<String>();
		List<String> paths = new ArrayList<String>();

		CommonsMultipartFile[] commonsMultipartFiles = fileUpload.getFiles();

		String filePath = null;

		for (CommonsMultipartFile multipartFile : commonsMultipartFiles) {
			filePath = "C:\\my_upload\\" + multipartFile.getOriginalFilename();
			File file = new File(filePath);

			// Copy files
			FileCopyUtils.copy(multipartFile.getBytes(), file);
			fileNames.add(multipartFile.getOriginalFilename());

			paths.add(filePath);
		}
		userService.process(paths);
		return fileNames;
	}
}
