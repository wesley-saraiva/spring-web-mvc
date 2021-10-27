package curso.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ProfissaoRepository profissaoRepository;

	@Autowired
	private ReportUtil reportUtil;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {

		ModelAndView modelView = new ModelAndView("cadastro/cadastropessoa");
		modelView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelView.addObject("pessoaobj", new Pessoa());
		modelView.addObject("profissoes", profissaoRepository.findAll());

		return modelView;
	}

	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoaPorPaginacao(@PageableDefault(size = 5) Pageable pageable,
			ModelAndView modelAndView, @RequestParam("nomepesquisa") String nomepesquisa) {

		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		modelAndView.addObject("pessoas", pagePessoa);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		modelAndView.setViewName("cadastro/cadastropessoa");

		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa", consumes = { "multipart/form-data" })
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file)
			throws IOException {

		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));

		if (bindingResult.hasErrors()) {
			ModelAndView modelView = new ModelAndView("cadastro/cadastropessoa");
			modelView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			modelView.addObject("pessoaobj", new Pessoa());

			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage());// vem das anotacoes @NotNull e @NotEmpty
			}

			modelView.addObject("msg", msg);
			modelView.addObject("profissoes", profissaoRepository.findAll());
			return modelView;
		}

		if (file.getSize() > 0) {// Cadastrando um curriculo
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		} else {
			if (pessoa.getId() != null && pessoa.getId() > 0) {// editando
				Pessoa pessoaTempo = pessoaRepository.findById(pessoa.getId()).get();
				pessoa.setCurriculo(pessoaTempo.getCurriculo());
				pessoa.setTipoFileCurriculo(pessoaTempo.getTipoFileCurriculo());
				pessoa.setNomeFileCurriculo(pessoaTempo.getNomeFileCurriculo());
			}
		}

		pessoaRepository.save(pessoa);

		ModelAndView modelView = new ModelAndView("cadastro/cadastropessoa");
		modelView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelView.addObject("pessoaobj", new Pessoa());

		return modelView;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {

		ModelAndView modelView = new ModelAndView("cadastro/cadastropessoa");
		modelView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelView.addObject("pessoaobj", new Pessoa());

		return modelView;
	}

	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {

		ModelAndView modelView = new ModelAndView("cadastro/cadastropessoa");
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		modelView.addObject("pessoaobj", pessoa.get());
		modelView.addObject("profissoes", profissaoRepository.findAll());

		return modelView;
	}

	@GetMapping("/excluirpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {

		pessoaRepository.deleteById(idpessoa);

		ModelAndView modelView = new ModelAndView("cadastro/cadastropessoa");
		modelView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelView.addObject("pessoaobj", new Pessoa());

		return modelView;
	}

	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesquisasexo") String pesquisasexo,
			@PageableDefault(size = 5, sort = { "nome" }) Pageable pageable) {

		Page<Pessoa> pessoas = null;

		if (pesquisasexo != null && !pesquisasexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaBySexoPage(nomepesquisa, pesquisasexo, pageable);
		} else {
			pessoas = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		}

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);

		return modelAndView;

	}

	@GetMapping("**/pesquisarpessoa")
	public void imprimePdf(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesquisasexo") String pesquisasexo, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		if (pesquisasexo != null && !pesquisasexo.isEmpty() && nomepesquisa != null && !nomepesquisa.isEmpty()) {// Busca
																													// sexo

			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesquisasexo);

		} else if (nomepesquisa != null && !nomepesquisa.isEmpty()) {// Busca somente por nome
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);

		} else if (pesquisasexo != null && !pesquisasexo.isEmpty()) {// Busca somente por nome
			pessoas = pessoaRepository.findPessoaBySexo(pesquisasexo);

		} else {// Busca todos
			Iterable<Pessoa> iterator = pessoaRepository.findAll();
			for (Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		// Chame o serviço que faz a geração do relatorio
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());

		// Tamanho da resposta
		response.setContentLength(pdf.length);

		// Definir na resposta o tipo de arquivo
		response.setContentType("application;octet-stream");

		// Definir o cabecalho da resposta
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);

		// Finaliza a resposta pro navegador
		response.getOutputStream().write(pdf);

	}

	@GetMapping("**/baixarcurriculo/{idpessoa}")
	public void baixarcurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response)
			throws IOException {

		// Consultar o objeto pessoa no banco e dados
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if (pessoa.getCurriculo() != null) {

			// Setar tamanho da resposta
			response.setContentLength(pessoa.getCurriculo().length);

			// Tipo do arquivo para dowload ou pode ser genrica application/octet-stream
			response.setContentType(pessoa.getTipoFileCurriculo());

			// Define o cabecalho da resposta
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);

			// Finaliza a resposta passando o arquivo
			response.getOutputStream().write(pessoa.getCurriculo());
		}
	}

}
