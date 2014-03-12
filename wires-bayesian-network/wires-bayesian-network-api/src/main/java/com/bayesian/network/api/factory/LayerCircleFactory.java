package com.bayesian.network.api.factory;

import org.kie.wires.core.client.util.ShapesUtils;

import com.emitrom.lienzo.client.core.event.NodeMouseDownEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseDownHandler;
import com.emitrom.lienzo.client.core.shape.Circle;
import com.emitrom.lienzo.client.core.shape.Group;
import com.emitrom.lienzo.client.core.shape.Layer;
import com.emitrom.lienzo.client.core.shape.Rectangle;
import com.emitrom.lienzo.client.core.shape.Shape;
import com.emitrom.lienzo.client.widget.LienzoPanel;

public class LayerCircleFactory extends LayerFactory<Circle> {

    private static final int RADIUS = 7;

    private static final String DESCRIPTION = "Circle";

    private static int layers;

    public LayerCircleFactory() {

    }

    public LayerCircleFactory(Group group, LienzoPanel panel, Integer lay) {
    	this.drawBoundingBox(group, null);
        layers = lay;
    }

    @Override
    public void drawBoundingBox(Group group, String template) {
        final Double x = this.getX() + 212;
        final Double y = this.getY() - 2;
        super.createOptions(x.intValue(), y.intValue());
        this.addBoundingHandlers(super.createBoundingBox(group, layers), group);
        this.addShapeHandlers(this.drawLayer(), group);
        group.add(super.createDescription(DESCRIPTION, layers));
    }

    @Override
    public Shape<Circle> drawLayer() {
        final Circle circle = new Circle(RADIUS);
        setAttributes(circle, this.getX(), this.getY());
        return circle;
    }

    private void setAttributes(Circle floatingShape, double x, double y) {
        floatingShape.setX(x).setY(y).setStrokeColor(ShapesUtils.RGB_STROKE_SHAPE)
                .setStrokeWidth(ShapesUtils.RGB_STROKE_WIDTH_SHAPE).setFillColor(ShapesUtils.RGB_FILL_SHAPE)
                .setDraggable(false);
    }

    @Override
    public void addShapeHandlers(Shape<Circle> shape, Group group) {
        shape.addNodeMouseDownHandler(this.getNodeMouseDownEvent(group));
        group.add(shape);

    }

    @Override
    protected void addBoundingHandlers(Rectangle boundingBox, Group group) {
        boundingBox.addNodeMouseDownHandler(getNodeMouseDownEvent(group));
    }

    @Override
    protected NodeMouseDownHandler getNodeMouseDownEvent(final Group group) {
        NodeMouseDownHandler nodeMouseDownHandler = new NodeMouseDownHandler() {
            public void onNodeMouseDown(NodeMouseDownEvent event) {
                // final EditableCircle floatingShape = new
                // EditableCircle(getFloatingX1(), getFloatingY1(),
                // getFloatingX2(), getFloatingY2());
                // floatingShape.setStrokeColor(ShapeFactoryUtil.RGB_STROKE_SHAPE)
                // .setStrokeWidth(ShapeFactoryUtil.RGB_STROKE_WIDTH_SHAPE).setDraggable(false);
                // setFloatingPanel(floatingShape, 30, 30, event, null);
            }
        };

        return nodeMouseDownHandler;

    }

    private double getX() {
        return 19;
    }

    private double getY() {
        return 15 + super.calculateY(layers);
    }

}