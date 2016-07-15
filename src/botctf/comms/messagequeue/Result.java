package botctf.comms.messagequeue;

/**
 * Represents the result of performing a requested action
 * @author nklaebe
 *
 */
public class Result {
	
	public static final Result SUCCESS = new Result(true,null);
	private boolean success;
	private String reason;
	
	public boolean isSuccess() {
		return success;
	}

	public String getReason() {
		return reason;
	}
	
	public Result(boolean success, String reason) {
		super();
		this.success = success;
		this.reason = reason;
	}
}
