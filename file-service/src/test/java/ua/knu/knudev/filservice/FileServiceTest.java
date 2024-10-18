package ua.knu.knudev.filservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileservice.service.FileService;
import ua.knu.knudev.fileserviceapi.dto.FileUploadPayload;
import ua.knu.knudev.fileserviceapi.exception.FileException;
import ua.knu.knudev.fileserviceapi.folder.FileFolder;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.subfolder.FileSubfolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    private static final String FOLDER_NAME = "test-folder";
    private static final String SUBFOLDER_PATH = "test-subfolder";
    private static final String TEST_FILE_EXTENSION = ".pdf";
    private static final String TEST_FILE_NAME = "test" + TEST_FILE_EXTENSION;
    private static final String DUMMY_CONTENT = "dummy content";

    @Mock
    private FileUploadAdapter fileUploadAdapter;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private FileFolderProperties<FileSubfolder> fileFolderProperties;

    @Mock
    private FileFolder<FileSubfolder> fileFolder;

    @Mock
    private FileSubfolder fileSubfolder;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() throws IOException {
        when(fileFolderProperties.getFolder()).thenReturn(fileFolder);
        when(fileFolderProperties.getSubfolder()).thenReturn(fileSubfolder);

        when(fileFolder.getName()).thenReturn(FOLDER_NAME);
        when(fileSubfolder.getSubfolderPath()).thenReturn(SUBFOLDER_PATH);

        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(DUMMY_CONTENT.getBytes()));
    }

    @Test
    @DisplayName("Should return generated file name when file is uploaded")
    void should_ReturnGeneratedFileName_When_FileIsUploaded() {
        when(fileUploadAdapter.saveFile(any(FileUploadPayload.class)))
                .thenReturn(UUID.randomUUID() + TEST_FILE_EXTENSION);
        mockMultipartFile();

        String result = uploadFile();

        assertNotNull(result, "Generated file name should not be null.");
        assertTrue(result.endsWith(TEST_FILE_EXTENSION),
                "Generated file name should end with the correct extension.");
    }

    @Test
    @DisplayName("Should call saveFile with correct payload when file is uploaded")
    void should_CallSaveFileWithCorrectPayload_When_FileIsUploaded() {
        mockMultipartFile();

        uploadFile();

        ArgumentCaptor<FileUploadPayload> captor = ArgumentCaptor.forClass(FileUploadPayload.class);
        verify(fileUploadAdapter).saveFile(captor.capture());
        FileUploadPayload capturedPayload = captor.getValue();

        assertEquals(FOLDER_NAME, capturedPayload.getFolderPath().getPath(),
                "Folder path should be correct."
        );
        assertEquals(SUBFOLDER_PATH, capturedPayload.getFolderPath().getSubfolderPath(),
                "Subfolder path should be correct."
        );
    }

    @Test
    @DisplayName("Should have non-null InputStream when file is uploaded")
    void should_HaveNonNullInputStream_When_FileIsUploaded() {
        mockMultipartFile();

        uploadFile();

        ArgumentCaptor<FileUploadPayload> captor = ArgumentCaptor.forClass(FileUploadPayload.class);
        verify(fileUploadAdapter).saveFile(captor.capture());

        FileUploadPayload capturedPayload = captor.getValue();
        assertNotNull(capturedPayload.getInputStream(), "Input stream should not be null.");
    }

    @Test
    @DisplayName("Should have correct InputStream content when file is uploaded")
    void should_HaveCorrectInputStreamContent_When_FileIsUploaded() throws IOException {
        mockMultipartFile();

        uploadFile();

        ArgumentCaptor<FileUploadPayload> captor = ArgumentCaptor.forClass(FileUploadPayload.class);
        verify(fileUploadAdapter).saveFile(captor.capture());

        FileUploadPayload capturedPayload = captor.getValue();
        String content = new String(capturedPayload.getInputStream().readAllBytes());
        assertEquals(DUMMY_CONTENT, content, "Input stream content should match expected content.");
    }

    @Test
    @DisplayName("Should throw exception when file is corrupted")
    void should_ThrowException_When_FileIsCorrupted() throws IOException {
        when(multipartFile.getInputStream()).thenThrow(new IOException("Test file corrupted exception"));

        FileException exception = assertThrows(FileException.class, this::uploadFile,
                "Expected FileException when input stream throws IOException.");
        assertEquals("Could not get input stream, because file is corrupted", exception.getMessage(),
                "Exception message should indicate that the file is corrupted.");
    }

    private void mockMultipartFile() {
        when(multipartFile.getOriginalFilename()).thenReturn(TEST_FILE_NAME);
    }

    private String uploadFile() {
        return fileService.uploadFile(multipartFile, fileFolderProperties);
    }
}
