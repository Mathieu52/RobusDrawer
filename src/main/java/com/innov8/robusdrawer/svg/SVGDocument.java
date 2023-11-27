package com.innov8.robusdrawer.svg;

import com.innov8.robusdrawer.draw.Drawing;
import com.innov8.robusdrawer.draw.DrawingPoint;
import com.innov8.robusdrawer.draw.PencilColor;
import com.innov8.robusdrawer.utils.FileUtils;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.parser.PathParser;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVGDocument extends File {
    private float offsetX, offsetY = 0;
    private float width;
    private float height;

    public SVGDocument(String pathname) {
        super(pathname);
        parse();
    }

    public SVGDocument(String parent, String child) {
        super(parent, child);
        parse();
    }

    public SVGDocument(File parent, String child) {
        super(parent, child);
        parse();
    }

    public SVGDocument(URI uri) {
        super(uri);
        parse();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private void parse() {
        String data = FileUtils.loadString(this);

        Matcher widthPattern = Pattern.compile("(?<=<svg)(?:.*?width=\\\")(\\d*)(?=(?:px)?\\\".*?\\/>)", Pattern.DOTALL).matcher(data);
        width = widthPattern.find() ? Integer.parseInt(widthPattern.group(1)) : 1;

        Matcher heightPattern = Pattern.compile("(?<=<svg)(?:.*?height=\\\")(\\d*)(?=(?:px)?\\\".*?\\/>)", Pattern.DOTALL).matcher(data);
        height = heightPattern.find() ? Integer.parseInt(heightPattern.group(1)) : 1;

        try {
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(
                    XMLResourceDescriptor.getXMLParserClassName());

            InputStream is = new FileInputStream(this);

            Document document = factory.createDocument(this.toURI().toURL().toString(), is);

            String viewBox = document.getDocumentElement().getAttribute("viewBox");
            String[] viewBoxValues = viewBox.split(" ");
            if (viewBoxValues.length > 3) {
                offsetX = Float.parseFloat(viewBoxValues[0]);
                offsetY = Float.parseFloat(viewBoxValues[1]);
                width = Float.parseFloat(viewBoxValues[2]);
                height = Float.parseFloat(viewBoxValues[3]);
            }
        } catch (IOException e) {
        }
    }

    public void load(Drawing drawing, PencilColor color) {
        PathParser parser = new PathParser();
        DrawingPathHandler handler = new DrawingPathHandler(drawing, getWidth(), getHeight());
        parser.setPathHandler(handler);

        drawing.clear();
        PencilColor originalColor = drawing.getPencilColor();
        drawing.setPencilColor(color);

        try {
            for (String path : getPaths()) {
                parser.parse(path);
            }
        } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        float widthScale = height > width ? 1.0f / height : 1.0f / width;;
        float heightScale = width > height ? 1.0f / width : 1.0f / height;
        float centerOffsetX = (1 - widthScale * width) / 2;
        float centerOffsetY = (1 - heightScale * height) / 2;
        for (DrawingPoint point : drawing.getDrawingPoints()) {
            point.setX((point.getX() - offsetX) * widthScale + centerOffsetX);
            point.setY((point.getY() -offsetY) * heightScale + centerOffsetY);
        }

        drawing.setPencilColor(originalColor);
    }

    private List<String> getPaths() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        String data = FileUtils.loadString(this);

        final String regex = "(?<=<path)(?:.*?\\bd=\\\")(?<path>.*?)(?:\\\".*?)(?=\\/>)";
        Matcher pathPattern = Pattern.compile(regex, Pattern.DOTALL).matcher(data);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        Document document = builder.parse(this);

        String xpathExpression = "//path[not(ancestor::clipPath)]/@d";

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile(xpathExpression);

        NodeList svgPaths = (NodeList)expression.evaluate(document, XPathConstants.NODESET);

        List<String> paths = new ArrayList<>();
        for (int i = 0; i < svgPaths.getLength(); i++) {
            paths.add(svgPaths.item(i).getNodeValue());
        }

        return paths;
    }
}
