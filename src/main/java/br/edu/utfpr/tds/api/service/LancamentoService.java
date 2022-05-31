package br.edu.utfpr.tds.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.edu.utfpr.tds.api.dto.LancamentoEstatisticaPessoa;
import br.edu.utfpr.tds.api.mail.Mailer;
import br.edu.utfpr.tds.api.model.Lancamento;
import br.edu.utfpr.tds.api.model.Pessoa;
import br.edu.utfpr.tds.api.model.Usuario;
import br.edu.utfpr.tds.api.repository.LancamentoRepository;
import br.edu.utfpr.tds.api.repository.PessoaRepository;
import br.edu.utfpr.tds.api.repository.UsuarioRepository;
import br.edu.utfpr.tds.api.service.exception.PessoaInexistenteOuInativaException;
import br.edu.utfpr.tds.api.storage.S3;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {
	
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
	
	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private Mailer mailer;
	
	@Autowired
	private S3 s3;
	
	//@Scheduled(fixedDelay = 1000 * 2) // 2 segundos
	//@Scheduled(cron = "0 3 11 * * *") // Especificar horários para ser executado - 11:03:00
//	@Scheduled(cron = "0 0 6 * * *") // Executar todo às 6h da manhã
//	public void avisarSobreLancamentosVencidos() {
//		System.out.println("----------- Método agendamento sendo executado -------------------");
//	}
	
	// -------- AGENDADO PARA ENVIAR EMAIL TODOS DIAS ÀS 06h00m
	@Scheduled(cron = "0 0 6 * * *")
	public void avisarSobreLancamentosVencidos() {
		if (logger.isDebugEnabled()) {
			logger.debug("Preparando envio de e-mails de aviso de lançamentos vencidos");
		}
		
		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		if (vencidos.isEmpty()) {
			logger.info("Sem lançamentos vencidos para aviso.");
			return;
		}
		
		logger.info("Existem {} lançamentos vencidos.", vencidos.size());
		
		List<Usuario> destinatarios = usuarioRepository
				.findByPermissoesDescricao(DESTINATARIOS);
		
		if (destinatarios.isEmpty()) {
			logger.warn("Existem lançamentos vencidos, mas o sistema não encontrou destinatários");
			return;
		} else {
			logger.info("O sistema encontrou {} destinatários", destinatarios.size());
		}
		
		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		
		logger.info("Envio de e-mail de aviso concluído.");
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoPeloCodigo(codigo);
		
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		if (StringUtils.isEmpty(lancamento.getAnexo())
				&& StringUtils.hasText(lancamentoSalvo.getAnexo())) {
			s3.remover(lancamentoSalvo.getAnexo());
		} else if (StringUtils.hasLength(lancamento.getAnexo())
				&& !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())) {
			s3.substituir(lancamentoSalvo.getAnexo(), lancamento.getAnexo());
		}

		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

		return lancamentoRepository.save(lancamentoSalvo);
		
//		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo"); // Copia os valores dos atributos de pessoa para pessoaSalva, exceto codigo
//		//pessoa.setCodigo(codigo);
//		
//		return this.lancamentoRepository.save(lancamentoSalvo);
	}
	
	// Comentado após criação de método de salva com anexo
//	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
//		Lancamento lancamentoSalvo = buscarLancamentoPeloCodigo(codigo);
//		
//		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
//			validarPessoa(lancamento);
//		}
//
//		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
//
//		return lancamentoRepository.save(lancamentoSalvo);
//		
////		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo"); // Copia os valores dos atributos de pessoa para pessoaSalva, exceto codigo
////		//pessoa.setCodigo(codigo);
////		
////		return this.lancamentoRepository.save(lancamentoSalvo);
//	}
	
	public Lancamento salvar(Lancamento lancamento) {
		Optional<Pessoa> pessoa = this.pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		
		if (!pessoa.isPresent() || pessoa.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
		// Salvar arquivo anexo permanantemente
		if (StringUtils.hasText(lancamento.getAnexo())) {
			s3.salvar(lancamento.getAnexo());
		}
		
		return this.lancamentoRepository.save(lancamento);
	}
	
	// Comentando após criação de método que salva com anexo 
//	public Lancamento salvar(Lancamento lancamento) {
//		Optional<Pessoa> pessoa = this.pessoaRepository.findById(lancamento.getPessoa().getCodigo());
//		
//		if (!pessoa.isPresent() || pessoa.get().isInativo()) {
//			throw new PessoaInexistenteOuInativaException();
//		}
//		
//		return this.lancamentoRepository.save(lancamento);
//	}
	
	private void validarPessoa(Lancamento lancamento) {
		Optional<Pessoa> pessoa = null;
		if (lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		}

		if (pessoa == null || pessoa.isPresent()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

	private Lancamento buscarLancamentoPeloCodigo(Long codigo) {
		Optional<Lancamento> lancamentoSalvoOpt = this.lancamentoRepository.findById(codigo);
		
		if (!lancamentoSalvoOpt.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		Lancamento lancamentoSalvo = lancamentoSalvoOpt.get();
		
		
		return lancamentoSalvo;
	}
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
		List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(inicio));
		parametros.put("DT_FIM", Date.valueOf(fim));
		parametros.put("REPORT_LOCALE", new Locale("pt", "BR")); // Utilizado para aplicar a formatação de valor definida no documento do relatório
		
		InputStream inputStream = this.getClass().getResourceAsStream(
				"/relatorios/lancamentos-por-pessoa.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
				new JRBeanCollectionDataSource(dados));
		
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}
}
