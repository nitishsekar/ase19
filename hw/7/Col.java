public class Col {
	
	private int oid;
	private String txt;
	private int pos;
	
	public Col() {
		int oid = -1;
		String txt = "";
		int pos = -1;
	}
	
	public Col(Col c) {
		this.oid = c.oid;
		this.txt = c.txt;
		this.pos = c.pos;
	}
	
	public Col(Sym s) {
		Col c = (Col) s;
		this.oid = c.oid;
		this.txt = c.txt;
		this.pos = c.pos;
	}
	
	public Col(Num n) {
		Col c = (Num) n;
		this.oid = c.oid;
		this.txt = c.txt;
		this.pos = c.pos;
	}
	
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public int getOid() {
		return oid;
	}
	public void setOid(int oid) {
		this.oid = oid;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
}
