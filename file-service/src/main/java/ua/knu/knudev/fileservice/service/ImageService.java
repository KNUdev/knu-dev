package ua.knu.knudev.fileservice.service;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileservice.config.ImageFileConfigProperties;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.folder.ImageFolder;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.knudevcommon.exception.FileException;

import java.io.IOException;

@Service
public class ImageService extends FileService implements ImageServiceApi {
    private final ImageFileConfigProperties imageFileConfigProperties;

    public ImageService(FileUploadAdapter fileUploadAdapter, ImageFileConfigProperties imageFileConfigProperties) {
        super(fileUploadAdapter);
        this.imageFileConfigProperties = imageFileConfigProperties;
    }

    @Override
    public String uploadFile(MultipartFile file, String customFilename, ImageSubfolder subfolder) {
        checkFileValidity(file);
        return uploadFile(file, customFilename, getProperties(subfolder));
    }

    @Override
    public String uploadFile(MultipartFile file, ImageSubfolder subfolder) {
        checkFileValidity(file);

        String filename = generateRandomUUIDFilename(file);
        return uploadFile(file, filename, getProperties(subfolder));
    }

    @Override
    public boolean existsByFilename(String filename, ImageSubfolder subfolder) {
        return existsByFilename(filename, getProperties(subfolder));
    }

    private void checkFileValidity(MultipartFile file) {
        if(ObjectUtils.isEmpty(file)) {
            throw new FileException("Cannot upload null image");
        }
        assertFileHasAllowedExtension(file, imageFileConfigProperties.allowedExtensions());
        checkFileSize(file);
    }

    private FileFolderProperties<ImageSubfolder> getProperties(ImageSubfolder imageSubfolder) {
        return FileFolderProperties.builder(ImageFolder.INSTANCE)
                .subfolder(imageSubfolder)
                .build();
    }

    //todo perhaps use this method also for pdf service
    private void checkFileSize(MultipartFile file) {
        try {
            int fileSizeInKilobytes = file.getBytes().length / 1024;
            final Integer MAX_SIZE_IN_KILOBYTES = imageFileConfigProperties.maximumSizeInKilobytes();

            if (fileSizeInKilobytes > MAX_SIZE_IN_KILOBYTES) {
                throw new FileException("File size is too big. Maximum allowed size is 2 megabytes");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
