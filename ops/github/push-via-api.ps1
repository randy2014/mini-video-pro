# Push local HEAD to GitHub via REST API (api.github.com).
#
# Why not plain `git push`: from this machine github.com:443 is unreachable
# (DNS resolves to 20.205.243.166, which is blocked) while api.github.com works,
# and the hosts file cannot be edited without admin rights.
#
# Auth: the credential already stored in Windows Credential Manager
#       (obtained via `git credential fill`; the token is never printed).
#
# Data files (kept separate so this script stays pure ASCII and immune to the
# Windows PowerShell 5.1 "UTF-8 file read as ANSI" problem):
#   files.txt   - changed file paths, one per line, repo-relative, UTF-8
#   message.txt - commit message, UTF-8
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$owner    = 'randy2014'
$repo     = 'mini-video-pro'
$repoRoot = 'F:\AI-PROD\mini-video-pro'
$here     = Split-Path -Parent $MyInvocation.MyCommand.Path
$expectBase = '4751f567'   # remote main before this push (sanity check)

function Get-Utf8Lines([string]$p) {
  return [System.IO.File]::ReadAllLines($p, [System.Text.Encoding]::UTF8)
}

# --- 1. token ---------------------------------------------------------------
$cred = "protocol=https`nhost=github.com`n`n" | git credential fill 2>$null
$tok  = ($cred | Select-String '^password=').ToString().Substring(9).Trim()
if (-not $tok) { throw 'no stored credential found' }

$H = @{
  Authorization          = "Bearer $tok"
  'User-Agent'           = 'dsh-agent'
  Accept                 = 'application/vnd.github+json'
  'X-GitHub-Api-Version' = '2022-11-28'
}
$JH = $H.Clone(); $JH['Content-Type'] = 'application/json; charset=utf-8'

function Invoke-GH([string]$Method, [string]$Path, $Body) {
  $uri = "https://api.github.com$Path"
  if ($null -eq $Body) {
    return Invoke-RestMethod -Method $Method -Uri $uri -Headers $H -TimeoutSec 60
  }
  $json  = $Body | ConvertTo-Json -Depth 10 -Compress
  $bytes = [System.Text.Encoding]::UTF8.GetBytes($json)
  return Invoke-RestMethod -Method $Method -Uri $uri -Headers $JH -Body $bytes -TimeoutSec 120
}

# --- 2. remote main ---------------------------------------------------------
$ref     = Invoke-GH 'GET' "/repos/$owner/$repo/git/ref/heads/main"
$baseSha = $ref.object.sha
Write-Host "remote main = $baseSha"
if ($baseSha -notlike "$expectBase*") {
  throw "remote main moved (expected $expectBase..., got $baseSha) - aborting to avoid clobbering"
}
$baseCommit = Invoke-GH 'GET' "/repos/$owner/$repo/git/commits/$baseSha"

# --- 3. blobs ---------------------------------------------------------------
$files = Get-Utf8Lines (Join-Path $here 'files.txt') | Where-Object { $_.Trim() -ne '' }
Write-Host "files to push: $($files.Count)"
$tree = @()
foreach ($rel in $files) {
  $abs = Join-Path $repoRoot ($rel -replace '/', '\')
  if (-not (Test-Path -LiteralPath $abs)) { throw "missing file: $abs" }
  $b64  = [Convert]::ToBase64String([System.IO.File]::ReadAllBytes($abs))
  $blob = Invoke-GH 'POST' "/repos/$owner/$repo/git/blobs" @{ content = $b64; encoding = 'base64' }
  $mode = if ($rel.ToLower().EndsWith('.sh')) { '100755' } else { '100644' }
  $tree += @{ path = $rel; mode = $mode; type = 'blob'; sha = $blob.sha }
  Write-Host ("  blob {0}  {1}" -f $blob.sha.Substring(0, 8), $rel)
}

# --- 4. tree + commit + move ref --------------------------------------------
$newTree = Invoke-GH 'POST' "/repos/$owner/$repo/git/trees" @{
  base_tree = $baseCommit.tree.sha
  tree      = $tree
}
$msg = ([System.IO.File]::ReadAllText((Join-Path $here 'message.txt'), [System.Text.Encoding]::UTF8)).TrimEnd()
$newCommit = Invoke-GH 'POST' "/repos/$owner/$repo/git/commits" @{
  message = $msg
  tree    = $newTree.sha
  parents = @($baseSha)
}
Write-Host "new commit = $($newCommit.sha)"

$upd = Invoke-GH 'PATCH' "/repos/$owner/$repo/git/refs/heads/main" @{
  sha   = $newCommit.sha
  force = $false
}
Write-Host "OK: main -> $($upd.object.sha)"
