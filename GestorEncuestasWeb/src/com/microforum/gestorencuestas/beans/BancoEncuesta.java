package com.microforum.gestorencuestas.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.microforum.gestorencuestas.entities.Encuesta;
import com.microforum.gestorencuestas.entities.Pregunta;

@ManagedBean(eager=true)
@ApplicationScoped
public class BancoEncuesta {

	//private Map<String,String> mapaPreguntas;
	private List<SelectItem> arrayEncuestasItems;
	private List<String> arrayEncuestas;
	private String proposito;
	private String ref;
	

	public BancoEncuesta(){
		
		Configuration conf=new Configuration();
		SessionFactory sf=conf.configure().buildSessionFactory();
		Session session=sf.openSession();
		Query query=session.createQuery("from Encuesta");
		List<Encuesta> listadoEncuestas=query.list();
		int tam=listadoEncuestas.size();
		arrayEncuestasItems=new ArrayList<SelectItem>();
		arrayEncuestasItems.add(new SelectItem("----Encuestas----"));
		arrayEncuestas = new ArrayList();
		for(int i=0;i<listadoEncuestas.size();i++){
			arrayEncuestas.add(listadoEncuestas.get(i).getRef());
			arrayEncuestas.add(listadoEncuestas.get(i).getProposito());
			String ref=listadoEncuestas.get(i).getRef();
			String propo=listadoEncuestas.get(i).getProposito();
			arrayEncuestasItems.add(new SelectItem(ref, propo));
		}
		session.close();
	}
	
	
	public List<SelectItem> getArrayEncuestasItems() {
		return arrayEncuestasItems;
	}


	public void setArrayEncuestasItems(List<SelectItem> arrayEncuestasItems) {
		this.arrayEncuestasItems = arrayEncuestasItems;
	}


	public List<String> getArrayEncuestas() {
		return arrayEncuestas;
	}


	public void setArrayEncuestas(List<String> arrayEncuestas) {
		this.arrayEncuestas = arrayEncuestas;
	}


	public String getProposito() {
		return proposito;
	}


	public void setProposito(String proposito) {
		this.proposito = proposito;
	}


	public String getRef() {
		return ref;
	}


	public void setRef(String ref) {
		this.ref = ref;
	}
	
	
	
	
}


