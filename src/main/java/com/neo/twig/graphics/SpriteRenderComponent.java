package com.neo.twig.graphics;

import com.neo.twig.TransformComponent;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.resources.ImageResource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

@SuppressWarnings("unused")
public final class SpriteRenderComponent extends RenderComponent {
    private TransformComponent transform;
    @ForceSerialize
    private ImageResource sprite;

    private double width = -1;
    private double height = -1;

    /**
     * Indicates if the sprite should have image filtering applied.
     */
    @ForceSerialize
    private boolean smooth = true;

    @Override
    public void start() {
        super.start();

        transform = getNode().getComponent(TransformComponent.class);
    }

    @Override
    public void destroy() {
        super.destroy();

        sprite.release();
    }

    public double getWidth() {
        if (width == -1)
            return sprite.get().getWidth();
        else
            return width;
    }

    public double getHeight() {
        if (height == -1)
            return sprite.get().getHeight();
        else
            return height;
    }

    public void setSprite(ImageResource resource) {
        sprite = resource;
    }

    @Override
    protected void drawToContext(GraphicsContext context) {
        context.save();
        //TODO: Support engine requested size, not fixed image size
        float lengthX = (float) (getWidth() * transform.scaleX);
        float lengthY = (float) (getHeight() * transform.scaleY);

        Rotate r = new Rotate(transform.rotation, transform.x, transform.y);
        context.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

        context.setImageSmoothing(smooth);

        //When given a negative length it inverts that side
        context.drawImage(sprite.get(), transform.x - (lengthX / 2), transform.y - (lengthY / 2), lengthX, lengthY);
        context.restore();
    }
}
