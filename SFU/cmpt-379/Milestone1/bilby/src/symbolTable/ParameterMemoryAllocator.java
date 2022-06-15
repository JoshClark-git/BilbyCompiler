package symbolTable;

import java.util.ArrayList;
import java.util.List;

public class ParameterMemoryAllocator implements MemoryAllocator {
	MemoryAccessMethod accessor;
	final int startingOffset;
	int currentOffset;
	int minOffset;
	String baseAddress;
	List<Integer> bookmarks;
	List<MemoryLocation> memoryLocations;
	
	public ParameterMemoryAllocator(MemoryAccessMethod accessor, String baseAddress, int startingOffset) {
		this.accessor = accessor;
		this.baseAddress = baseAddress;
		this.startingOffset = startingOffset;
		this.currentOffset = startingOffset;
		this.minOffset = startingOffset;
		this.bookmarks = new ArrayList<Integer>();
		this.memoryLocations = new ArrayList<MemoryLocation>();
	}
	public ParameterMemoryAllocator(MemoryAccessMethod accessor,String baseAddress) {
		this(accessor, baseAddress, 0);
	}

	@Override
	public MemoryLocation allocate(int sizeInBytes) {
		currentOffset -= sizeInBytes;
		updateMin();
		MemoryLocation newLocation = new MemoryLocation(accessor, baseAddress, currentOffset);
		memoryLocations.add(newLocation);
		return newLocation;
	}
	public MemoryLocation allocateFP(int sizeInBytes) {
		currentOffset -= sizeInBytes;
		updateMin();
		MemoryLocation newLocation = new MemoryLocation(accessor, baseAddress, currentOffset);
		return newLocation;
	}
	private void updateMin() {
		if(minOffset > currentOffset) {
			minOffset = currentOffset;
		}
	}

	@Override
	public String getBaseAddress() {
		return baseAddress;
	}

	@Override
	public int getMaxAllocatedSize() {
		return startingOffset - minOffset;
	}
	@Override
	public int getOffset() {
		return currentOffset;
	}
	
	@Override
	public void saveState() {
		bookmarks.add(currentOffset);
	}
	@Override
	public void restoreState() {
		assert bookmarks.size() > 0;
		int bookmarkIndex = bookmarks.size()-1;
		currentOffset = (int) bookmarks.remove(bookmarkIndex);
		if(bookmarks.size() == 0) {
			int offsetAdder = getMaxAllocatedSize();
			for(int i = 0; i < memoryLocations.size(); i++) {
				int oldOffset = memoryLocations.get(i).getOffset();
				memoryLocations.get(i).setOffset(oldOffset + offsetAdder);
			}
		}
	}
}
