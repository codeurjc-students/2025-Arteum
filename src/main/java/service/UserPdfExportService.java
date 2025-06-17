package service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import model.Artwork;
import model.Review;
import model.User;

@Service
public class UserPdfExportService {

    @Autowired
    private UserService userService;

    public byte[] generateUserPdf(Long userId) throws IOException {
        User user = userService.findById(userId).orElseThrow();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Header
        doc.add(new Paragraph("Perfil de Usuario")
                .setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Generado el: " + sdf.format(new Date()))
                .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
        doc.add(new Paragraph("\n"));
        
     // Imagen del usuario
        if (user.getImage() != null && user.getImage().length > 0) {
            try {
                Image image = new Image(ImageDataFactory.create(user.getImage()));
                image.setWidth(120);
                image.setHeight(120);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                image.setBorder(new SolidBorder(ColorConstants.GRAY, 1));
                image.setMarginBottom(15);
                doc.add(image);
            } catch (Exception e) {
                doc.add(new Paragraph("⚠️ Imagen inválida o corrupta.").setFontColor(ColorConstants.RED));
            }
        }

        // Datos personales
        doc.add(new Paragraph("🧍 Información del usuario").setBold().setFontSize(14));
        doc.add(new Paragraph("Nombre: " + user.getName()));
        doc.add(new Paragraph("Email: " + user.getEmail()));
        doc.add(new Paragraph("Localización: " + (user.getLocation() != null ? user.getLocation() : "No especificada")));
        doc.add(new Paragraph("Biografía: " + (user.getBiography() != null ? user.getBiography() : "No especificada")));
        doc.add(new Paragraph("Fecha de creación: " + sdf.format(user.getCreatedAt())));
        doc.add(new Paragraph("Seguidores: " + user.getFollowers().size()));
        doc.add(new Paragraph("Siguiendo: " + user.getFollowing().size()));
        doc.add(new Paragraph("\n"));

        // Obras favoritas
        doc.add(new Paragraph("⭐ Obras Favoritas").setBold().setFontSize(14));
        if (user.getFavoriteArtworks().isEmpty()) {
            doc.add(new Paragraph("No tiene obras favoritas."));
        } else {
            for (Artwork a : user.getFavoriteArtworks()) {
                String title = a.getTitle();
                String artist = a.getArtist() != null ? a.getArtist().getName() : "Desconocido";
                doc.add(new Paragraph("- " + title + " (" + artist + ")"));
            }
        }
        doc.add(new Paragraph("\n"));

        // Reseñas
        doc.add(new Paragraph("📝 Reseñas Realizadas").setBold().setFontSize(14));
        doc.add(new Paragraph("Reseñas: " + user.getReviews().size()));
        doc.add(new Paragraph("Reseña media: " + round(user.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0), 2)));
        if (user.getReviews().isEmpty()) {
            doc.add(new Paragraph("No ha hecho reseñas."));
        } else {
            Table table = new Table(new float[] {4, 1, 6, 2});
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell("Obra").addHeaderCell("⭐").addHeaderCell("Comentario").addHeaderCell("Fecha");

            for (Review r : user.getReviews()) {
                table.addCell(r.getArtwork().getTitle());
                table.addCell(String.valueOf(r.getRating()));
                table.addCell(r.getComment() != null ? r.getComment() : "-");
                table.addCell(sdf.format(r.getCreatedAt()));
            }
            doc.add(table);
        }
        doc.add(new Paragraph("\n"));
        
        // Seguidores
        doc.add(new Paragraph("Seguidores").setBold().setFontSize(14));
        doc.add(new Paragraph("Número: " + user.getFollowers().size()));
        if (user.getFollowers().isEmpty()) {
            doc.add(new Paragraph("No hay seguidores."));
        } else {
        	List list = new com.itextpdf.layout.element.List();

            for (User u : user.getFollowing()) {
                list.add(new ListItem(u.getName()));
            }

            doc.add(list);
        }
        doc.add(new Paragraph("\n"));
        
        // Seguidos
        doc.add(new Paragraph("Seguidos").setBold().setFontSize(14));
        doc.add(new Paragraph("Número: " + user.getFollowing().size()));
        if (user.getFollowing().isEmpty()) {
        	doc.add(new Paragraph("No hay seguidos."));
        } else {
            List list = new com.itextpdf.layout.element.List();

            for (User u : user.getFollowing()) {
                list.add(new ListItem(u.getName()));
            }

            doc.add(list);

        }

        doc.close();
        return baos.toByteArray();
    }
    
    private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
