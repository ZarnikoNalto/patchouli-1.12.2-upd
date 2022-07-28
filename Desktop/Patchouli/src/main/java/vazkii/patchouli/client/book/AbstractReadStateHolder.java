package vazkii.patchouli.client.book;

import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractReadStateHolder {

	transient EntryDisplayState readState;
	transient boolean readStateDirty = true;
	
	public EntryDisplayState getReadState() {
		if(readStateDirty) {
			readState = computeReadState();
			readStateDirty = false;
		}
		
		return readState;
	}
	
	public void markReadStateDirty() {
		readStateDirty = true;
	}
	
	protected abstract EntryDisplayState computeReadState();
	
	public static EntryDisplayState mostImportantState(Stream<EntryDisplayState>... streams) {
		Stream<EntryDisplayState> stream = Streams.concat(streams);
		return EntryDisplayState.fromOrdinal(stream.mapToInt(EntryDisplayState::ordinal).min().orElse(EntryDisplayState.DEFAULT_TYPE.ordinal()));
	}
	
}
