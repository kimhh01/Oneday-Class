package kr.co.oneclass.author.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService {

    // 저장된 파일을 가리키는 웹 경로 접두사. DB 에는 이 형태로 들어간다
    public static final String WEB_PREFIX = "/upload/author/";

    // 업로드 허용 확장자와 최대 크기
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    // 파일 저장 기본 경로
    private final Path uploadRoot;

    public LocalFileStorageService(
            @Value("${oneday.author.upload-root:uploads/author}") String uploadRootPath) {
        this.uploadRoot = Paths.get(uploadRootPath).toAbsolutePath().normalize();
    }

    // 파일을 저장하고 저장 경로를 반환한다
    public String store(MultipartFile file, String directory) {
        validate(file);

        String extension = extensionOf(file.getOriginalFilename());
        String savedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        // directory 는 코드에서 넘기는 고정값이지만 경로 이탈은 막아둔다
        Path targetDirectory = resolveInsideRoot(directory);

        try {
            Files.createDirectories(targetDirectory);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetDirectory.resolve(savedName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }

        return WEB_PREFIX + directory + "/" + savedName;
    }

    // 저장된 파일을 삭제한다
    public void delete(String filePath) {
        // 이 서비스가 저장한 경로만 삭제 대상으로 삼는다
        if (filePath == null || !filePath.startsWith(WEB_PREFIX)) {
            return;
        }

        Path target = resolveInsideRoot(filePath.substring(WEB_PREFIX.length()));
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new IllegalStateException("파일 삭제에 실패했습니다.", e);
        }
    }

    // 업로드 가능한 파일인지 검사한다
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("파일 크기는 5MB 를 넘을 수 없습니다.");
        }
        extensionOf(file.getOriginalFilename());
    }

    // 허용 확장자만 통과시키고 소문자로 돌려준다
    private String extensionOf(String originalFilename) {
        if (originalFilename == null) {
            throw new IllegalArgumentException("파일명을 확인할 수 없습니다.");
        }

        int dot = originalFilename.lastIndexOf('.');
        String extension = dot < 0 ? "" : originalFilename.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("jpg, jpeg, png 만 업로드할 수 있습니다.");
        }
        return extension;
    }

    // uploadRoot 를 벗어나는 경로를 차단한다 (../ 등)
    private Path resolveInsideRoot(String relativePath) {
        Path resolved = uploadRoot.resolve(relativePath).normalize();
        if (!resolved.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("허용되지 않은 경로입니다.");
        }
        return resolved;
    }
}
