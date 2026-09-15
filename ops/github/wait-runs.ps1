# Poll GitHub Actions runs until they finish (ASCII only - PS 5.1 safe).
# Usage: powershell -File wait-runs.ps1 -RunIds 123,456 [-TimeoutMin 25]
param(
  [Parameter(Mandatory = $true)][string]$RunIds,
  [int]$TimeoutMin = 25
)
$ErrorActionPreference = 'Stop'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$cred = "protocol=https`nhost=github.com`n`n" | git credential fill 2>$null
$tok  = ($cred | Select-String '^password=').ToString().Substring(9).Trim()
$H = @{
  Authorization          = "Bearer $tok"
  'User-Agent'           = 'dsh-agent'
  Accept                 = 'application/vnd.github+json'
  'X-GitHub-Api-Version' = '2022-11-28'
}

$ids = $RunIds.Split(',') | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne '' }
$deadline = (Get-Date).AddMinutes($TimeoutMin)
$pending = @{}; foreach ($i in $ids) { $pending[$i] = $true }

while ((Get-Date) -lt $deadline -and $pending.Count -gt 0) {
  foreach ($id in @($pending.Keys)) {
    try {
      $r = Invoke-RestMethod "https://api.github.com/repos/randy2014/mini-video-pro/actions/runs/$id" -Headers $H -TimeoutSec 40
      Write-Host ("[{0}] {1} ({2}) status={3} conclusion={4}" -f (Get-Date -Format 'HH:mm:ss'), $r.name, $id, $r.status, $r.conclusion)
      if ($r.status -eq 'completed') {
        Write-Host "  ==> RUN $id FINISHED: $($r.conclusion)"
        Write-Host "  ==> $($r.html_url)"
        $pending.Remove($id) | Out-Null
      }
    } catch { Write-Host "  poll error for ${id}: $($_.Exception.Message)" }
  }
  if ($pending.Count -gt 0) { Start-Sleep -Seconds 25 }
}
if ($pending.Count -gt 0) { Write-Host "TIMEOUT waiting for: $($pending.Keys -join ',')" } else { Write-Host "ALL RUNS COMPLETED" }
