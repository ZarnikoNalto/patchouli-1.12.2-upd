package vazkii.patchouli.client.book.text;

import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public class Span {
	public static Span error(SpanState state, String message) {
		return new Span(state, message, 0xFF0000, "");
	}

	public final FontRenderer font;
	public final String text;
	public final int color;
	public final String codes;
	public final List<Span> linkCluster;
	public final String tooltip;
	public final Runnable onClick;
	public final int lineBreaks;
	public final int spacingLeft;
	public final int spacingRight;
	public final boolean bold;

	public Span(SpanState state, String text) {
		this.font = state.font;
		this.text = text;
		this.color = state.color;
		this.codes = state.codes;
		this.onClick = state.onClick;
		this.linkCluster = state.cluster;
		this.tooltip = state.tooltip;
		this.lineBreaks = state.lineBreaks;
		this.spacingLeft = state.spacingLeft;
		this.spacingRight = state.spacingRight;
		this.bold = codes.contains("\u00A7l");

		state.lineBreaks = 0;
		state.spacingLeft = 0;
		state.spacingRight = 0;
	}

	private Span(SpanState state, String text, int color, String codes) {
		this.font = state.font;
		this.text = text;
		this.color = color;
		this.codes = codes;
		this.onClick = null;
		this.linkCluster = null;
		this.tooltip = "";
		this.lineBreaks = state.lineBreaks;
		this.spacingLeft = state.spacingLeft;
		this.spacingRight = state.spacingRight;
		this.bold = codes.contains("\u00A7l");

		state.lineBreaks = 0;
		state.spacingLeft = 0;
		state.spacingRight = 0;
	}
}
