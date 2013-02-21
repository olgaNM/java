package com.microforum.gestorencuestas.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.microforum.gestorencuestas.entities.Administrador;
import com.microforum.gestorencuestas.entities.Encuesta;
import com.microforum.gestorencuestas.entities.Pregunta;
import com.microforum.gestorencuestas.entities.Usuario;
import com.microforum.gestorencuestas.entities.UsuarioRegistrado;

/**
 * @author alumno
 *
 */
@ManagedBean
@SessionScoped
public class EncuestaSesion {
	//private List<String> preguntasEncuesta=new ArrayList<String>();
	//preguntas de las encuestas
	private List<RegistroEncuesta> registrosEncuesta=
			new ArrayList<RegistroEncuesta>();
	private String nuevaPregunta="";
	private String visibilidadDetalle="display: none;";

	private List<RegistroEncuesta> registrosEncuestaUsuario=
			new ArrayList<RegistroEncuesta>();
	private String proposito;
	
	
	public List<RegistroEncuesta> getRegistrosEncuestaUsuario() {
		return registrosEncuestaUsuario;
	}

	public String registrarEncuestaUsuario(){
		System.out.println("dentro de registrarEncuestaUsuario");
		FacesContext fContext=FacesContext.getCurrentInstance();
		ExternalContext ec=fContext.getExternalContext();
		HttpServletRequest request=(HttpServletRequest) ec.getRequest();
		HttpSession session=request.getSession();
		Object obj=session.getAttribute("userBean");
		if(obj==null){
			System.out.println("dentro de registrarEncuestaUsuario dentro de if");
			session.invalidate();
			return "/index.jsp";
		}else{
			System.out.println("dentro de registrarEncuestaUsuario dentro de else");
			UsuarioAutenticado authUser=(UsuarioAutenticado)obj;
			UsuarioRegistrado user=(UsuarioRegistrado) authUser.getUser();
			Configuration conf=new Configuration();
			SessionFactory sf=conf.configure().buildSessionFactory();
			Session hSession=sf.openSession();
			Transaction tr=hSession.beginTransaction();
			Query query=hSession.createQuery(
					"from Pregunta where ref in (:preguntasRef)");
			List<String> refList=getRefList(registrosEncuestaUsuario);
			query.setParameterList("preguntasRefEncuesta", refList);
			List<Pregunta> preguntas=query.list();
			hSession.merge(user);
			
/*			Encuesta encuesta=new Encuesta();
			encuesta.setAutor(user);
			encuesta.setProposito(proposito);
			for(Pregunta p:preguntas){
				encuesta.getPreguntas().add(p);
			}*/
			
		//	hSession.save(encuesta);
			tr.commit();
			hSession.close();
			registrosEncuestaUsuario.clear();
			proposito=null;
			return "/encuestas/usuarios/UsuarioRegistradoIndex";
		}
		
	}
	
	
	
	
	
	
	
//	private String nombre;
//	private int valoracion;
	
	private List<String> getRefList(List<RegistroEncuesta> regList){
		List<String> refList=new ArrayList<String>();
		for(RegistroEncuesta re:regList){
			refList.add(re.getRef());
		}
		return refList;
	}
	public String registrarEncuesta(){
		FacesContext fContext=FacesContext.getCurrentInstance();
		ExternalContext ec=fContext.getExternalContext();
		HttpServletRequest request=(HttpServletRequest) ec.getRequest();
		HttpSession session=request.getSession();
		Object obj=session.getAttribute("userBean");
		if(obj==null){
			session.invalidate();
			return "/index.jsp";
		}else{
			UsuarioAutenticado authUser=(UsuarioAutenticado)obj;
			Administrador user=(Administrador) authUser.getUser();
			Configuration conf=new Configuration();
			SessionFactory sf=conf.configure().buildSessionFactory();
			Session hSession=sf.openSession();
			Transaction tr=hSession.beginTransaction();
			Query query=hSession.createQuery(
					"from Pregunta where ref in (:preguntasRef)");
			List<String> refList=getRefList(registrosEncuesta);
			query.setParameterList("preguntasRef", refList);
			List<Pregunta> preguntas=query.list();
			hSession.merge(user);
			Encuesta encuesta=new Encuesta();
			encuesta.setAutor(user);
			encuesta.setProposito(proposito);
			for(Pregunta p:preguntas){
				encuesta.getPreguntas().add(p);
			}
			hSession.save(encuesta);
			tr.commit();
			hSession.close();
			registrosEncuesta.clear();
			proposito=null;
			return "/encuestas/administracion/administracionIndex";
		}
		
	}
	
	public void validarNombre(FacesContext context,
			UIComponent componentToValidate,
			Object value)throws ValidatorException{
		String nombre=(String)value;
		if(!nombre.startsWith("Encuesta")){
			FacesMessage msg=
					new FacesMessage("El nombre debe empezar por Encuesta");
			throw new ValidatorException(msg);
		}
	}
	
	public String getProposito() {
		return proposito;
	}
	public void setProposito(String proposito) {
		this.proposito = proposito;
	}
	public String iniciarEncuesta(){
		return "/encuestas/administracion/DetalleEncuesta";
	}
	
	public String iniciarEncuestaUsuario(){
		System.out.println("dentro de iniciarEncuestaUsuario");
		return "/encuestas/usuarios/DetalleEncuestaUsuario";
	}
	
	public String terminarEncuesta(){
		//agregar control de navegacion
		return "/encuestas/administracion/resumenEncuesta";
	}
	
	public void salvarEncuesta(){
		Configuration conf=new Configuration();
		SessionFactory sf=conf.configure().buildSessionFactory();
		Session session=sf.openSession();
		Query query=session.createQuery("from Pregunta where ref in (:preguntasRef)");
		
	}
	public void eliminarPregunta(ActionEvent e){
		UIComponent component=e.getComponent();
		component= component.findComponent("preguntaRef");
		if(component!=null){
			if(component instanceof UIParameter){
				UIParameter parameter=(UIParameter)component;
				RegistroEncuesta reg=(RegistroEncuesta) parameter.getValue();
				registrosEncuesta.remove(reg);
				if(registrosEncuesta.isEmpty())
					visibilidadDetalle="display: none;";
			}
		}
	}
	
	public String getVisibilidadDetalle() {
		return visibilidadDetalle;
	}

	public void setVisibilidadDetalle(String visibilidadDetalle) {
		this.visibilidadDetalle = visibilidadDetalle;
	}

	public List<RegistroEncuesta> getRegistrosEncuesta() {
		return registrosEncuesta;
	}

	public String getNuevaPregunta() {
		return nuevaPregunta;
	}

	public void setNuevaPregunta(String nuevaPregunta) {
		this.nuevaPregunta = nuevaPregunta;
	}

	public void addPregunta(ActionEvent e){
		System.out.println("ActionEvent");
	}
	
	public void selectPregunta(ValueChangeEvent e){
		System.out.println(e.getNewValue());
		System.out.println("ValueChangeEvent");
		String ref=(String) e.getNewValue();
		Configuration conf=new Configuration();
		SessionFactory sf=conf.configure().buildSessionFactory();
		Session session=sf.openSession();
		Pregunta p=(Pregunta) session.get(Pregunta.class,ref);
		if(p!=null){
			visibilidadDetalle="display:inline-block;";
			RegistroEncuesta re=new RegistroEncuesta();
			re.setRef(p.getRef());
			re.setTexto(p.getTexto());
			re.setTipo(re.getTipo());
			registrosEncuesta.add(re);
		}
		session.close();
		//preguntasEncuesta.add((String) e.getNewValue());
	}
	
	public void selectEncuesta(ValueChangeEvent e){
	//	System.out.println(e.getNewValue());
		System.out.println("ValueChangeEvent selectEncuesta");
		String ref=(String) e.getNewValue();
		Configuration conf=new Configuration();
		SessionFactory sf=conf.configure().buildSessionFactory();
		Session session=sf.openSession();
		Encuesta en =(Encuesta) session.get(Encuesta.class, ref);
		//Pregunta p=(Pregunta) session.get(Pregunta.class,ref);
		if(en!=null){
			
			
		//	visibilidadDetalle="display:inline-block;";
			RegistroEncuesta re=new RegistroEncuesta();
			re.setRef(en.getRef());
			re.setTexto(en.getProposito());
			//re.setTipo(en.get)Tipo(en.getPreguntas());
			registrosEncuesta.add(re);
		}
		session.close();
		//preguntasEncuesta.add((String) e.getNewValue());
	}

	
	
}
