package p2p.visibility.nuv.map;

public class Map {

	private Patch[][] patches;

	private int patchWidth;
	private int patchHeight;

	public Map(Patch patch, int patchWidth, int patchHeight) {

		this.patchWidth = patchWidth;
		this.patchHeight = patchHeight;

		int colNum = patch.getWidth() / patchWidth;
		int rowNum = patch.getHeight() / patchHeight;

		patches = new Patch[colNum][rowNum];

		for (int x = 0; x < rowNum; x++) {

			for (int y = 0; y < colNum; y++) {

				int xCoord = x * patchWidth;
				int yCoord = y * patchWidth;

				patches[x][y] = patch.getPart(xCoord, yCoord, patchWidth, patchHeight);
			}
		}
	}

	public Patch getPatch(int x, int y) {

		return patches[x][y];
	}
	
	public Patch getEmptyPatch(int x, int y) {
		
		return new Patch(x * this.patchWidth, y * this.patchHeight, this.patchWidth, this.patchHeight);
	}

	public Patch getPatchAt(int x, int y) {

		return patches[x / patchWidth][y / patchHeight];
	}
	
	public Patch getEmptyPatchAt(int x, int y) {
		
		return new Patch(x / patchWidth * patchWidth, y / patchHeight * patchHeight, this.patchWidth, this.patchHeight);
	}
}
