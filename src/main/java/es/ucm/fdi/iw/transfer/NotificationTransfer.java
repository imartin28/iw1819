package es.ucm.fdi.iw.transfer;

import es.ucm.fdi.iw.model.Notification;

public class NotificationTransfer {

	private Long id;
	private String text;
	
	
	
	
	public NotificationTransfer(Notification notificacion) {
		this.id = notificacion.getId();
		this.text = notificacion.getText();
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	
	
}
