package cidc.convenios.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import cidc.general.db.BaseDB;
import cidc.general.db.CursorDB;
import cidc.general.mails.EnvioMail2;
import cidc.general.mails.Reporte;
import cidc.general.obj.CrearClaves;
import cidc.general.obj.Globales;
import cidc.proyectosGeneral.obj.ExtraDocProyecto;
import cidc.proyectosGeneral.obj.Proyecto;
import cidc.convenios.obj.DatosAjax;


import cidc.adminArticulos.obj.Articulo;
import cidc.adminArticulos.obj.DatEvaluador;
import cidc.adminArticulos.obj.EstadoArticulo;
import cidc.adminArticulos.obj.FiltroArticulo;
import cidc.convenios.obj.Convenio;
import cidc.convenios.obj.GetConvenioOBJ;
import cidc.convenios.obj.ObservacionesOBJ;



public class AdminConvenioDB extends BaseDB{
	GetConvenioOBJ conv=null;

	public AdminConvenioDB(CursorDB c, int perfil) {
		super(c, perfil);
		// TODO Auto-generated constructor stub
		rb=ResourceBundle.getBundle("cidc.convenios.consultas");
	}
	
	public boolean insertaObservacion(int idPro, String observacion,long usuario) {
		Connection cn=null;
		PreparedStatement ps=null;
		boolean retorno = false;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("insertaObservacion"));
			ps.setLong(i++,idPro);
			ps.setString(i++,observacion);
			ps.setLong(i++,usuario);
			ps.executeUpdate();
			retorno = true;
		}catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}
	
	public boolean nuevaCargaDocConvenio(ExtraDocProyecto documento, Proyecto proyecto,long idUsuario) {
		boolean retorno=false;
		Connection cn=null;
		PreparedStatement ps=null;
		boolean noGenerico=true;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			cn.setAutoCommit(false);
			if(proyecto.getClaseProyecto()==2){
				switch (documento.getTipo()) {
					case 1:
					case 3:
						noGenerico=true;//el case 1 y 3 no son tan necesarios pero los dejo por si acaso		
					break;
					case 2:
						ps=cn.prepareStatement(rb.getString("cargaInformeFinalProyecto2"));
						ps.setString(i++, documento.getNombreArchivo());
						ps.setString(i++, documento.getFechaDoc());
						ps.setString(i++, documento.getObservaciones());
						ps.setInt(i++, proyecto.getId());
						ps.executeUpdate();
						retorno=true;
						noGenerico=false;
					break;
					case 4://si es caso 3 o 4 es el mismo procedimiento
					case 5:
						ps=cn.prepareStatement(rb.getString("cargaActaCierre2"));
						ps.setString(i++, documento.getNombreArchivo());
						ps.setString(i++, documento.getFechaDoc());
						ps.setString(i++, documento.getObservaciones());
						ps.setInt(i++, proyecto.getId());
						ps.executeUpdate();
						retorno=true;
						noGenerico=false;
					break;
				
				}
			}
			i=1;
			if(noGenerico){
				if(documento.getTipo()==3)
					documento.setNombreDocumento("Informe Parcial");
				if(documento.getTipo()==2)
					documento.setNombreDocumento("Informe Final");
				
				ps=cn.prepareStatement(rb.getString("nuevaCargaDocProyecto"+proyecto.getClaseProyecto()));
				ps.setLong(i++, proyecto.getId());			
				ps.setString(i++, documento.getNombreDocumento());
				ps.setString(i++, documento.getFechaDoc());
				ps.setString(i++, documento.getNombreArchivo());						
				ps.setString(i++, documento.getObservaciones());
				ps.setInt(i++, documento.getTipo());
				ps.setInt(i++, documento.getEstado());
				ps.setLong(i++, idUsuario);			
				ps.executeUpdate();
				retorno=true;
			}
			i=1;
			
			if(documento.getTipo()==4 || documento.getTipo()==5){
				ps=cn.prepareStatement(rb.getString("cambiaEstadoProyecto"+proyecto.getClaseProyecto()));
				if(documento.getTipo()==4) 	ps.setInt(i++, 3);
				if(documento.getTipo()==5) 	ps.setInt(i++, 4);
				ps.setInt(i++, proyecto.getId());			
				ps.executeUpdate();
			//	System.out.println(".--- sentenci de cambio--->"+ps);
			}
			cn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}
	
	public boolean cambiaEstado(int idconv, String tipo, String estado ) {
		Connection cn=null;
		PreparedStatement ps=null;
		boolean retorno = false;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("cambiaEstadoconvenio"));
			ps.setString(i++,estado);
			ps.setString(i++,tipo);
			ps.setLong(i++,idconv);
			ps.executeUpdate();
			retorno = true;
		}catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}
	
	

	public boolean nuevoConvenio(Convenio convenio) {
		
		
		boolean retorno=false;
		Connection cn=null;
		PreparedStatement ps=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("nuevoConvenio2"));
			ps.setLong(i++,convenio.getCodigo());
			ps.setString(i++, convenio.getFecha());
			ps.setString(i++,convenio.getNombreConvenio());
			ps.setString(i++,convenio.getObservaciones());
			ps.setString(i++, convenio.getEstado());
			ps.setInt(i++,convenio.getV_DuraAnos());
			ps.setInt(i++,convenio.getV_Durameses());
			ps.setInt(i++,convenio.getV_Duradias());
			ps.setString(i++, convenio.getFechaInicio());
			ps.setString(i++, convenio.getTipo());
			ps.setInt(i++,convenio.getNumDisp());
		    ps.setString(i++, convenio.getFechaFinalizacion());
			ps.setFloat(i++, convenio.getVEfectivo());
			ps.setFloat(i++, convenio.getVEspecie());
			ps.setString(i++, convenio.getN_UsuDigita());
			ps.setString(i++, convenio.getF_Digita());
			ps.setString(i++, convenio.getNombreproyecto());
			ps.setInt(i++,convenio.getEstadop());
			ps.setInt(i++,convenio.getTipop());
			ps.setInt(i++, convenio.getFacultad());
			ps.setInt(i++, convenio.getProycurri());
			ps.setInt(i++, convenio.getGrupo());
			ps.setString(i++, convenio.getObjetivo());
			ps.setString(i++, convenio.getResumen());
			ps.setString(i++, convenio.getObservacionesp());
			ps.executeUpdate();
			retorno=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}
	
	 public List consultarInvestigadores (int cod)
	 {
	  List listaInvestigadores = new ArrayList();
	  Connection cn = null;
	  PreparedStatement ps = null;
	  ResultSet rs = null;
      DatosAjax datos = null;
      try{
          cn = cursor.getConnection(super.perfil);
          ps = cn.prepareStatement(rb.getString("consultainvestigadores"));
          ps.setInt(1, cod);
          rs = ps.executeQuery();
          while (rs.next())
          {
           datos = new DatosAjax();
           datos.setCodigo(rs.getInt(1));
           datos.setNombre(rs.getString(2)); //select cod,nombres||' '||apellidos
           listaInvestigadores.add(datos);
          }
         }
      catch (SQLException e){
            lanzaExcepcion(e);
            }
      catch (Exception e){
    	     lanzaExcepcion(e);
            }

      finally {
    	  try{
    		  rs.close();
    		  ps.close();
    		  cn.close();
    	  }
    	  catch (Exception e){}

	 }
      return listaInvestigadores;
	 }
	 
	 public int idconvenio(String nombre){
		    Connection cn=null;
			PreparedStatement ps=null;
			ResultSet rs=null;
			int id = 0;
			Convenio convenio= null;
		 try{
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("buscarconvenioID"));
			ps.setString(1, nombre);
			rs=ps.executeQuery();
			while(rs.next()){
				id=rs.getInt("pk_codconvenionum");
			}
			 
			 
		 }catch(Exception e){
			 lanzaExcepcion(e);
			 
		 }finally{
				cerrar(ps);
				cerrar(cn);
			}
		 
		 return id;
		 
	 }

	public List listaConvenio() {
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		List lista=new ArrayList();
		Convenio convenio= null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("listaConvenio2"));
			rs=ps.executeQuery();
			while(rs.next()){
				i=1;
				convenio= new Convenio();
				convenio.setIdconvenio(rs.getInt(i++));
				convenio.setCodigo(rs.getInt(i++));
				convenio.setFecha(rs.getString(i++));
				convenio.setNombreConvenio(rs.getString(i++));
				convenio.setObservaciones(rs.getString(i++));
				convenio.setEstado(rs.getString(i++));
				convenio.setV_DuraAnos(rs.getInt(i++));
				convenio.setV_Durameses(rs.getInt(i++));
				convenio.setV_Duradias(rs.getInt(i++));
				convenio.setFechaInicio(rs.getString(i++));
				convenio.setTipo(rs.getString(i++));
				convenio.setNumDisp(rs.getInt(i++));
				convenio.setFechaFinalizacion(rs.getString(i++));
				convenio.setVEfectivo(rs.getFloat(i++));
				convenio.setVEspecie(rs.getFloat(i++));
				convenio.setN_UsuDigita(rs.getString(i++));
				convenio.setF_Digita(rs.getString(i++));
				convenio.setNombreproyecto(rs.getString(i++));
				convenio.setEstadop(rs.getInt(i++));
				convenio.setTipop(rs.getInt(i++));
				convenio.setFacultad(rs.getInt(i++));
				convenio.setProycurri(rs.getInt(i++));
				convenio.setGrupo(rs.getInt(i++));
				convenio.setObjetivo(rs.getString(i++));
				convenio.setResumen(rs.getString(i++));
				convenio.setObservacionesp(rs.getString(i++));
				lista.add(convenio);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("se dano esta joda");
			lanzaExcepcion(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return lista;
	}
	
	 public List consultaProyectos()
		{
		 List listaProyectos = new ArrayList();
		 Connection cn = null;
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 DatosAjax datos = null;

	     try {
			  cn =  cursor.getConnection(super.perfil);
			  ps = cn.prepareStatement(rb.getString("consultaproyectos"));
	          rs = ps.executeQuery();
	          while (rs.next())
	          {
	           datos = new DatosAjax();
	           datos.setCodigo(rs.getInt(1));
	           datos.setNombre(rs.getString(2));
	           listaProyectos.add(datos);
	          }
		     } catch (SQLException e) {
	         // TODO Auto-generated catch block
	         lanzaExcepcion(e);
	        }catch (Exception e) {
			 lanzaExcepcion(e);
		    }
		     finally {
		            try {
		             rs.close();
		             ps.close();
		             cn.close();
		            }
		            catch (Exception e){}
		     }
	     return listaProyectos;
		}
	 
	 
	 
	 public List consultarGrupos (int cod)
	 {
	  List listaGrupos = new ArrayList();
	  Connection cn = null;
	  PreparedStatement ps = null;
	  ResultSet rs = null;
      DatosAjax datos = null;
      try{
    	  
          cn = cursor.getConnection(super.perfil);
          ps = cn.prepareStatement(rb.getString("consultagrupos"));
         
          ps.setInt(1, cod);
          rs = ps.executeQuery();
        
          while (rs.next())
          {
           datos = new DatosAjax();
           datos.setCodigo(rs.getInt(1));
           datos.setNombre(rs.getString(2)); //select cod,nombres||' '||apellidos
           listaGrupos.add(datos);
          
          }
         }
      catch (SQLException e){
            lanzaExcepcion(e);
            }
      catch (Exception e){
    	     lanzaExcepcion(e);
    	     
            }

      finally {
    	  try{
    		  rs.close();
    		  ps.close();
    		  cn.close();
    	  }
    	  catch (Exception e){}

	 }
      return listaGrupos;
	 }
	 
	 
	 public GetConvenioOBJ buscarConvenio(int id){
		 
			conv=getConvenio(id);
			
			if(conv!=null){
				
				conv.setListaObservaciones(getListaObservaciones(id));
			
			}
			return conv;
		}
	 
	public GetConvenioOBJ getConvenio(int id) {
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		GetConvenioOBJ convenio= null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getConvenio2"));
			ps.setInt(i++,id);
			rs=ps.executeQuery();
			while(rs.next()){
				i=1;

				convenio= new GetConvenioOBJ();
				convenio.setIdconvenio(rs.getString(i++));
				convenio.setCodigo(rs.getString(i++));
				convenio.setFecha(rs.getString(i++));
				convenio.setNombreConvenio(rs.getString(i++));
				convenio.setObservaciones(rs.getString(i++));
				convenio.setEstado(rs.getString(i++));
				convenio.setV_DuraAnos(rs.getString(i++));
				convenio.setV_Durameses(rs.getString(i++));
				convenio.setV_Duradias(rs.getString(i++));
				convenio.setFechaInicio(rs.getString(i++));
				convenio.setTipo(rs.getString(i++));
				convenio.setNumDisp(rs.getString(i++));
				convenio.setFechaFinalizacion(rs.getString(i++));
				convenio.setVEfectivo(rs.getString(i++));
				convenio.setVEspecie(rs.getString(i++));
				convenio.setN_UsuDigita(rs.getString(i++));
				convenio.setF_Digita(rs.getString(i++));
				convenio.setNombreproyecto(rs.getString(i++));
				convenio.setEstadop(rs.getString(i++));
				convenio.setTipop(rs.getString(i++));
				convenio.setFacultad(rs.getString(i++));
				convenio.setProycurri(rs.getString(i++));
				convenio.setGrupo(rs.getString(i++));
				convenio.setObjetivo(rs.getString(i++));
				convenio.setResumen(rs.getString(i++));
				convenio.setObservacionesp(rs.getString(i++));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("se jodio por el ID");
			lanzaExcepcion(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return convenio;
	}
	
	public Convenio getConvenioo(int id) {
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		Convenio convenio= null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getConvenio2"));
			ps.setInt(i++,id);
			rs=ps.executeQuery();
			while(rs.next()){
				i=1;
				
			    convenio= new Convenio();
			    convenio.setIdconvenio(rs.getInt(i++));
				convenio.setCodigo(rs.getInt(i++));
				convenio.setFecha(rs.getString(i++));
				convenio.setNombreConvenio(rs.getString(i++));
				convenio.setObservaciones(rs.getString(i++));
				convenio.setEstado(rs.getString(i++));
				convenio.setV_DuraAnos(rs.getInt(i++));
				convenio.setV_Durameses(rs.getInt(i++));
				convenio.setV_Duradias(rs.getInt(i++));
                convenio.setFechaInicio(rs.getString(i++));
 				convenio.setTipo(rs.getString(i++));
                convenio.setNumDisp(rs.getInt(i++));
				convenio.setFechaFinalizacion(rs.getString(i++));
				convenio.setVEfectivo(rs.getFloat(i++));
				convenio.setVEspecie(rs.getFloat(i++));

				convenio.setN_UsuDigita(rs.getString(i++));
				convenio.setF_Digita(rs.getString(i++));
				convenio.setNombreproyecto(rs.getString(i++));
				convenio.setEstadop(rs.getInt(i++));
				convenio.setTipop(rs.getInt(i++));
				convenio.setFacultad(rs.getInt(i++));
				convenio.setProycurri(rs.getInt(i++));
				convenio.setGrupo(rs.getInt(i++));

				convenio.setObjetivo(rs.getString(i++));
				convenio.setResumen(rs.getString(i++));
				convenio.setObservacionesp(rs.getString(i++));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("se jodio por el ID");
			lanzaExcepcion(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return convenio;
	}

	public boolean actualizaConvenio(Convenio convenio) {
		boolean retorno=false;
		Connection cn=null;
		PreparedStatement ps=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("actualizaConvenio"));
			ps.setInt(i++,convenio.getEstado());
			ps.setInt(i++,convenio.getNumero());
			ps.setString(i++,convenio.getNombreConvenio());
			ps.setString(i++,convenio.getEntidades());
			ps.setString(i++,convenio.getFecha());
			ps.setString(i++,convenio.getValor());
			ps.setInt(i++,convenio.getAno());
			ps.setString(i++,convenio.getCompromisos());
			ps.setString(i++,convenio.getObservaciones());
			ps.setLong(i++,convenio.getIdConvenio());
			ps.executeUpdate();
			retorno=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}
	
	
	public List getListaObservaciones(long idConv) {
		
		PreparedStatement ps=null;
		ResultSet rs=null;
		Connection cn=null;
		List  lista=new ArrayList ();
		ObservacionesOBJ observ=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getObservacionesConv"));
			ps.setLong(1,idConv);
			
			
			rs=ps.executeQuery();
		
			while(rs.next()){
				i=1;
				observ= new ObservacionesOBJ();
				observ.setIdObservacion(rs.getLong(i++));
				observ.setFecha(rs.getString(i++));
				observ.setObservacion(rs.getString(i++));
				observ.setUsuario(rs.getString(i++));
				lista.add(observ);
			}
		}catch (SQLException e) {
			
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
			cerrar(cn);
		}
		return lista;
	}
}


