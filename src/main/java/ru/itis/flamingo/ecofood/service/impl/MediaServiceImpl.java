package ru.itis.flamingo.ecofood.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.flamingo.ecofood.domain.dto.ProductDto;
import ru.itis.flamingo.ecofood.domain.entity.Image;
import ru.itis.flamingo.ecofood.domain.repository.ImageRepository;
import ru.itis.flamingo.ecofood.domain.repository.ProductRepository;
import ru.itis.flamingo.ecofood.domain.repository.UserRepository;
import ru.itis.flamingo.ecofood.mapper.ProductMapper;
import ru.itis.flamingo.ecofood.service.FileService;
import ru.itis.flamingo.ecofood.service.MediaService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    @Qualifier("dropbox")
    private final FileService fileService;

    @Override
    @Transactional
    public void downloadProfilePhoto(String username, MultipartFile file) {
        var fileName = generateFileName(file.getOriginalFilename());
        var mainPhoto = new Image()
            .setName(fileName);
        var currentUser = userRepository.findUserByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User with username " + username + " not found"));
        currentUser.setMainPhoto(mainPhoto);
        userRepository.save(currentUser);
        fileService.saveFile(file, fileName);
    }

    @Override
    public byte[] getProfilePhoto(String username) {
        var user = userRepository.findUserByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User with username " + username + " not found"));
        var profilePhoto = user.getMainPhoto();
        if (profilePhoto == null || profilePhoto.getName() == null) {
            throw new IllegalArgumentException("User has not profile photo");
        }
        return fileService.getFile(profilePhoto.getName());
    }

    @Override
    public byte[] getMedia(String name) {
        return fileService.getFile(name);
    }

    @Override
    @Transactional
    public Image downloadImage(MultipartFile multipartFile) {
        var savedFileName = generateFileName(multipartFile.getOriginalFilename());
        var mainPhoto = new Image()
            .setName(savedFileName);
        var image = imageRepository.save(mainPhoto);
        fileService.saveFile(multipartFile, savedFileName);
        return image;
    }

    @Override
    public ProductDto uploadProductImages(Long productId, List<MultipartFile> multipartFileList) {
        var product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product with id " + productId + " not found"));
        List<Image> productImage = new ArrayList<>();
        for (MultipartFile file : multipartFileList) {
            productImage.add(downloadImage(file));
        }
        product.setImages(productImage);
        product = productRepository.save(product);
        return productMapper.mapToDto(product);
    }

    private String generateFileName(String originalName) {
        if (originalName == null) {
            throw new IllegalArgumentException("Original file name is null");
        }
        var fileName = UUID.randomUUID();
        var fileResolution = originalName.substring(originalName.lastIndexOf("."));
        return fileName + fileResolution;
    }

}
