package com.flowerable.spring.service.gemini;

import com.flowerable.spring.dto.buquet.BouquetPreviewReq;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BouquetPromptBuilder {

    public String buildFromPreviewReq(BouquetPreviewReq req) {
        StringBuilder sb = new StringBuilder();
        appendFlowerLines(sb, req.getOrderItems().stream().map(item ->
                new FlowerLine(item.getQuantity(), item.getFlowerName(), item.getFlowerColor())
        ).toList());
        appendWrappingAndStyle(sb, req.getWrappingColorName());
        return sb.toString();
    }

    private void appendFlowerLines(StringBuilder sb, List<FlowerLine> lines) {
        sb.append("A realistic professional bouquet made of:\n");
        for (FlowerLine line : lines) {
            sb.append("- ")
                    .append(line.quantity())
                    .append(" ")
                    .append(line.flowerName())
                    .append(" in ")
                    .append(line.color())
                    .append("\n");
        }
    }

    private void appendWrappingAndStyle(StringBuilder sb, String wrappingColor) {
        if (wrappingColor != null && !wrappingColor.isBlank()) {
            sb.append("Wrapped with ").append(wrappingColor).append(" paper\n");
        } else {
            sb.append("No wrapping paper, bare bouquet\n");
        }
        sb.append("soft natural lighting, product photography, white background");
    }

    private record FlowerLine(int quantity, String flowerName, String color) {}
}