package org.openwis.metadataportal.services.login;


import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OpenWisImageCaptchaServlet extends HttpServlet {
    public static ImageCaptchaService service = new DefaultManageableImageCaptchaService();

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        httpServletResponse.setDateHeader("Expires", 0L);

        httpServletResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

        httpServletResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");

        httpServletResponse.setHeader("Pragma", "no-cache");

        httpServletResponse.setContentType("image/jpeg");

        BufferedImage bi = service.getImageChallengeForID(httpServletRequest.getSession(true).getId());

        ServletOutputStream out = httpServletResponse.getOutputStream();

        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        httpServletResponse.setDateHeader("Expires", 0L);

        httpServletResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

        httpServletResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");

        httpServletResponse.setHeader("Pragma", "no-cache");

        httpServletResponse.setContentType("image/jpeg");

        BufferedImage bi = service.getImageChallengeForID(httpServletRequest.getSession(true).getId());

        ServletOutputStream out = httpServletResponse.getOutputStream();

        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    public static boolean validateResponse(HttpServletRequest request, String userCaptchaResponse) {
        if (request.getSession(false) == null) return false;

        boolean validated = false;
        try {
            validated = service.validateResponseForID(request.getSession().getId(), userCaptchaResponse).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return validated;
    }
}
