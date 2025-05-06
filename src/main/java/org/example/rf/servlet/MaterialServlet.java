package org.example.rf.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.rf.dao.SubjectDAO;
import org.example.rf.model.Subject;
import org.json.JSONObject;
import org.example.rf.dao.MaterialDAO;
import org.example.rf.model.Material;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet("/material")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class MaterialServlet extends HttpServlet {

    // Thư mục lưu trữ các file PDF đã tải lên (trên server Java)
    private static final String UPLOAD_DIRECTORY = "uploads";
    private final String FASTAPI_URL = "http://localhost:8001"; // Thay bằng URL FastAPI của bạn

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 1. Lấy thông tin từ form
            String title = request.getParameter("title");
            String chapterId = request.getParameter("chapterId");
            String type = "PDF";

            if (title == null || title.isEmpty() || chapterId == null || chapterId.isEmpty()) {
                out.println("Thiếu thông tin cần thiết!");
                return;
            }

            // 2. Xử lý upload file
            Part filePart = request.getPart("pdfFile");
            String fileName = getSubmittedFileName(filePart);

            if (!fileName.toLowerCase().endsWith(".pdf")) {
                out.println("Chỉ chấp nhận file PDF!");
                return;
            }

            // 3. Tạo ID duy nhất cho material
            String materialId = UUID.randomUUID().toString();

            // 4. Tạo thư mục uploads (nếu chưa tồn tại)
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 5. Tạo tên file duy nhất để tránh trùng lặp
            String uniqueFileName = materialId + "-" + fileName;
            String filePath = uploadPath + File.separator + uniqueFileName;  // Đường dẫn PDF trên server JAVA

            // 6. Lưu file lên server JAVA
            filePart.write(filePath);

            // 7. Gọi FastAPI để xử lý PDF và tạo VectorDB
            String vectorDbPath = processPdfWithFastAPI(filePath, chapterId);

            // 8. Tạo đối tượng Material
            Material material = new Material(materialId, title, filePath, chapterId, type, vectorDbPath);

            // 9. Lưu thông tin vào database (bao gồm cả đường dẫn PDF VÀ đường dẫn VectorDB)
            boolean inserted = materialDAO.insertMaterial(material);

            if (inserted) {
                // 10. Hiển thị thông báo thành công
                out.println("<div class='success'>Tải lên PDF thành công!</div>");
                out.println("<p>Material ID: " + materialId + "</p>");
                out.println("<a href='material-list.jsp'>Quay lại danh sách tài liệu</a>");
            } else {
                out.println("<div class='error'>Lỗi khi thêm material vào database.</div>");
            }

        } catch (Exception e) {
            out.println("<div class='error'>Lỗi khi xử lý: " + e.getMessage() + "</div>");
            e.printStackTrace();
        }
    }

    private String processPdfWithFastAPI(String filePath, String chapterId) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(FASTAPI_URL + "/process");

        // Tạo payload form data
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("pdf_filename", filePath));
        params.add(new BasicNameValuePair("chapter_title", chapterId));

        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8")); // Set form data

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String responseString = EntityUtils.toString(responseEntity, "UTF-8");

        JSONObject jsonResponse = new JSONObject(responseString);
        return jsonResponse.getString("vector_db_path");
    }

    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Subject> subjects = subjectDAO.getAllSubjects();

        request.setAttribute("subjects", subjects);

        // Không cần truyền chapters mặc định nữa vì sẽ load theo môn
        request.getRequestDispatcher("/upload-material.jsp").forward(request, response);
    }
}